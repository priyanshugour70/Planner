package com.lssgoo.planner.data.sync

import android.content.Context
import android.content.SharedPreferences
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.remote.S3Manager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced Backup Manager with detailed progress tracking and history
 */
class BackupManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "backup_manager_prefs"
        private const val KEY_BACKUP_HISTORY = "backup_history"
        private const val KEY_LAST_BACKUP = "last_backup_time"
        private const val KEY_LAST_RESTORE = "last_restore_time"
        private const val MAX_HISTORY_ENTRIES = 20
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val storageManager = LocalStorageManager(context)
    private val s3Manager = S3Manager(context)
    private val gson = Gson()
    
    // Backup state
    private val _backupState = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()
    
    /**
     * Create a full backup to S3
     */
    suspend fun createBackup(): BackupResult = withContext(Dispatchers.IO) {
        _backupState.value = BackupState(status = BackupStatus.IN_PROGRESS, progress = 0.1f)
        
        try {
            // Step 1: Export all data
            _backupState.value = _backupState.value.copy(
                currentStep = "Exporting data...",
                progress = 0.2f
            )
            val jsonData = storageManager.exportAllData()
            
            // Step 2: Calculate backup size
            _backupState.value = _backupState.value.copy(
                currentStep = "Preparing upload...",
                progress = 0.4f
            )
            val backupSize = jsonData.length
            
            // Step 3: Upload to S3
            _backupState.value = _backupState.value.copy(
                currentStep = "Uploading to cloud...",
                progress = 0.6f
            )
            val (success, error) = s3Manager.uploadToS3(jsonData)
            
            if (success) {
                // Step 4: Save backup metadata
                _backupState.value = _backupState.value.copy(
                    currentStep = "Finalizing...",
                    progress = 0.9f
                )
                val timestamp = System.currentTimeMillis()
                saveBackupToHistory(BackupEntry(
                    timestamp = timestamp,
                    type = BackupType.CLOUD,
                    status = BackupEntryStatus.SUCCESS,
                    sizeBytes = backupSize.toLong()
                ))
                prefs.edit().putLong(KEY_LAST_BACKUP, timestamp).apply()
                
                _backupState.value = BackupState(
                    status = BackupStatus.SUCCESS,
                    progress = 1f,
                    lastBackupTime = timestamp
                )
                
                BackupResult(
                    success = true,
                    timestamp = timestamp,
                    sizeBytes = backupSize.toLong()
                )
            } else {
                val errorMsg = error ?: "Upload failed"
                _backupState.value = BackupState(
                    status = BackupStatus.FAILED,
                    error = errorMsg
                )
                saveBackupToHistory(BackupEntry(
                    timestamp = System.currentTimeMillis(),
                    type = BackupType.CLOUD,
                    status = BackupEntryStatus.FAILED,
                    errorMessage = errorMsg
                ))
                BackupResult(success = false, error = errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown error"
            _backupState.value = BackupState(
                status = BackupStatus.FAILED,
                error = errorMsg
            )
            BackupResult(success = false, error = errorMsg)
        }
    }
    
    /**
     * Restore from S3 backup
     */
    suspend fun restoreBackup(): BackupResult = withContext(Dispatchers.IO) {
        _backupState.value = BackupState(status = BackupStatus.IN_PROGRESS, progress = 0.1f)
        
        try {
            // Step 1: Check for backup
            _backupState.value = _backupState.value.copy(
                currentStep = "Checking for backup...",
                progress = 0.2f
            )
            
            // Step 2: Download from S3
            _backupState.value = _backupState.value.copy(
                currentStep = "Downloading from cloud...",
                progress = 0.4f
            )
            val (data, error) = s3Manager.downloadFromS3()
            
            if (data != null) {
                // Step 3: Import data
                _backupState.value = _backupState.value.copy(
                    currentStep = "Restoring data...",
                    progress = 0.7f
                )
                val success = storageManager.importAllData(data)
                
                if (success) {
                    val timestamp = System.currentTimeMillis()
                    prefs.edit().putLong(KEY_LAST_RESTORE, timestamp).apply()
                    
                    _backupState.value = BackupState(
                        status = BackupStatus.SUCCESS,
                        progress = 1f,
                        lastRestoreTime = timestamp
                    )
                    
                    BackupResult(success = true, timestamp = timestamp)
                } else {
                    _backupState.value = BackupState(
                        status = BackupStatus.FAILED,
                        error = "Failed to import data"
                    )
                    BackupResult(success = false, error = "Failed to import data")
                }
            } else if (error != null) {
                _backupState.value = BackupState(
                    status = BackupStatus.FAILED,
                    error = error
                )
                BackupResult(success = false, error = error)
            } else {
                _backupState.value = BackupState(
                    status = BackupStatus.FAILED,
                    error = "No backup found"
                )
                BackupResult(success = false, error = "No backup found")
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown error"
            _backupState.value = BackupState(
                status = BackupStatus.FAILED,
                error = errorMsg
            )
            BackupResult(success = false, error = errorMsg)
        }
    }
    
    /**
     * Get backup history
     */
    fun getBackupHistory(): List<BackupEntry> {
        val historyJson = prefs.getString(KEY_BACKUP_HISTORY, null) ?: return emptyList()
        return try {
            gson.fromJson(historyJson, Array<BackupEntry>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get last backup time
     */
    fun getLastBackupTime(): Long? {
        val time = prefs.getLong(KEY_LAST_BACKUP, 0L)
        return if (time > 0) time else null
    }
    
    /**
     * Get last restore time
     */
    fun getLastRestoreTime(): Long? {
        val time = prefs.getLong(KEY_LAST_RESTORE, 0L)
        return if (time > 0) time else null
    }
    
    /**
     * Get formatted last backup time
     */
    fun getLastBackupTimeFormatted(): String {
        val time = getLastBackupTime() ?: return "Never"
        val format = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        return format.format(Date(time))
    }
    
    /**
     * Reset backup state
     */
    fun resetState() {
        _backupState.value = BackupState()
    }
    
    private fun saveBackupToHistory(entry: BackupEntry) {
        val history = getBackupHistory().toMutableList()
        history.add(0, entry)
        
        // Keep only the most recent entries
        val trimmedHistory = history.take(MAX_HISTORY_ENTRIES)
        
        prefs.edit()
            .putString(KEY_BACKUP_HISTORY, gson.toJson(trimmedHistory))
            .apply()
    }
}

/**
 * Current backup operation state
 */
data class BackupState(
    val status: BackupStatus = BackupStatus.IDLE,
    val progress: Float = 0f,
    val currentStep: String = "",
    val error: String? = null,
    val lastBackupTime: Long? = null,
    val lastRestoreTime: Long? = null
)

/**
 * Backup operation status
 */
enum class BackupStatus {
    IDLE,
    IN_PROGRESS,
    SUCCESS,
    FAILED
}

/**
 * Result of a backup operation
 */
data class BackupResult(
    val success: Boolean,
    val timestamp: Long? = null,
    val sizeBytes: Long? = null,
    val error: String? = null
)

/**
 * Single backup entry in history
 */
data class BackupEntry(
    val timestamp: Long,
    val type: BackupType,
    val status: BackupEntryStatus,
    val sizeBytes: Long? = null,
    val errorMessage: String? = null
) {
    fun getFormattedDate(): String {
        val format = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        return format.format(Date(timestamp))
    }
    
    fun getFormattedSize(): String {
        val size = sizeBytes ?: return ""
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
}

/**
 * Backup type
 */
enum class BackupType {
    CLOUD,
    LOCAL
}

/**
 * Backup entry status
 */
enum class BackupEntryStatus {
    SUCCESS,
    FAILED
}
