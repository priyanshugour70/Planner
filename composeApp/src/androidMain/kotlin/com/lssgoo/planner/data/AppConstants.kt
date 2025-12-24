package com.lssgoo.planner.data

import com.lssgoo.planner.BuildConfig

/**
 * AppConstants holds all global constants and configuration values.
 * It uses values from BuildConfig (populated from s3.properties or environment variables).
 */
object AppConstants {
    // S3 Configuration
    const val S3_BUCKET_NAME = BuildConfig.S3_BUCKET_NAME
    const val S3_ACCESS_KEY = BuildConfig.S3_ACCESS_KEY
    const val S3_SECRET_KEY = BuildConfig.S3_SECRET_KEY
    const val S3_REGION = BuildConfig.S3_REGION
    
    // Backup settings
    const val BACKUP_FILE_PREFIX = "planner_backup_"
    const val BACKUP_FILE_EXTENSION = ".json"
}
