package com.lssgoo.planner.data.sync

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.lssgoo.planner.data.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * AutoSyncManager handles automatic background synchronization with AWS S3.
 * Provides configurable sync intervals and battery-friendly sync options.
 */
class AutoSyncManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "auto_sync_prefs"
        private const val KEY_SYNC_ENABLED = "sync_enabled"
        private const val KEY_SYNC_INTERVAL = "sync_interval_minutes"
        private const val KEY_SYNC_ON_WIFI_ONLY = "sync_wifi_only"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_SYNC_STATUS = "sync_status"
        
        const val WORK_NAME_PERIODIC = "planner_auto_sync"
        const val WORK_NAME_IMMEDIATE = "planner_immediate_sync"
        
        // Sync interval presets (in minutes)
        val SYNC_INTERVAL_REALTIME = 15L       // Every 15 minutes
        val SYNC_INTERVAL_FREQUENT = 30L       // Every 30 minutes
        val SYNC_INTERVAL_HOURLY = 60L         // Every hour
        val SYNC_INTERVAL_EVERY_3_HOURS = 180L // Every 3 hours
        val SYNC_INTERVAL_DAILY = 1440L        // Once a day
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Enable automatic sync with specified interval
     */
    fun enableAutoSync(intervalMinutes: Long = SYNC_INTERVAL_HOURLY, wifiOnly: Boolean = false) {
        prefs.edit()
            .putBoolean(KEY_SYNC_ENABLED, true)
            .putLong(KEY_SYNC_INTERVAL, intervalMinutes)
            .putBoolean(KEY_SYNC_ON_WIFI_ONLY, wifiOnly)
            .apply()
        
        schedulePeriodicSync(intervalMinutes, wifiOnly)
    }
    
    /**
     * Disable automatic sync
     */
    fun disableAutoSync() {
        prefs.edit()
            .putBoolean(KEY_SYNC_ENABLED, false)
            .apply()
        
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PERIODIC)
    }
    
    /**
     * Check if auto sync is enabled
     */
    fun isAutoSyncEnabled(): Boolean = prefs.getBoolean(KEY_SYNC_ENABLED, false)
    
    /**
     * Get current sync interval
     */
    fun getSyncInterval(): Long = prefs.getLong(KEY_SYNC_INTERVAL, SYNC_INTERVAL_HOURLY)
    
    /**
     * Get last sync time
     */
    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
    
    /**
     * Update last sync time
     */
    fun updateLastSyncTime(time: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, time).apply()
    }
    
    /**
     * Get sync status
     */
    fun getSyncStatus(): SyncStatus {
        val statusOrdinal = prefs.getInt(KEY_SYNC_STATUS, SyncStatus.IDLE.ordinal)
        return SyncStatus.entries.getOrElse(statusOrdinal) { SyncStatus.IDLE }
    }
    
    /**
     * Update sync status
     */
    fun updateSyncStatus(status: SyncStatus) {
        prefs.edit().putInt(KEY_SYNC_STATUS, status.ordinal).apply()
    }
    
    /**
     * Schedule periodic background sync
     */
    private fun schedulePeriodicSync(intervalMinutes: Long, wifiOnly: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .setRequiresBatteryNotLow(true)
            .build()
        
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES) // Give app time to initialize
            .addTag("auto_sync")
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }
    
    /**
     * Trigger an immediate sync
     */
    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag("immediate_sync")
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_IMMEDIATE,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
    
    /**
     * Get sync interval display name
     */
    fun getIntervalDisplayName(intervalMinutes: Long): String {
        return when (intervalMinutes) {
            SYNC_INTERVAL_REALTIME -> "Every 15 minutes"
            SYNC_INTERVAL_FREQUENT -> "Every 30 minutes"
            SYNC_INTERVAL_HOURLY -> "Every hour"
            SYNC_INTERVAL_EVERY_3_HOURS -> "Every 3 hours"
            SYNC_INTERVAL_DAILY -> "Once a day"
            else -> "Custom ($intervalMinutes min)"
        }
    }
}

/**
 * Sync status enum
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    FAILED,
    PENDING
}

/**
 * Background Worker for performing sync operations
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val autoSyncManager = AutoSyncManager(applicationContext)
        val syncRepository = SyncRepository(applicationContext)
        
        autoSyncManager.updateSyncStatus(SyncStatus.SYNCING)
        
        try {
            val (success, error) = syncRepository.syncToCloud()
            
            if (success) {
                autoSyncManager.updateLastSyncTime()
                autoSyncManager.updateSyncStatus(SyncStatus.SUCCESS)
                Result.success()
            } else {
                autoSyncManager.updateSyncStatus(SyncStatus.FAILED)
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            autoSyncManager.updateSyncStatus(SyncStatus.FAILED)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
