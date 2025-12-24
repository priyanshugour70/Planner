package com.lssgoo.planner.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

/**
 * Android Settings factory using SharedPreferences
 */
private var appContext: Context? = null

fun initializeSettings(context: Context) {
    appContext = context.applicationContext
}

actual fun createSettings(): Settings {
    val context = appContext 
        ?: throw IllegalStateException("Settings not initialized. Call initializeSettings() first.")
    val sharedPrefs = context.getSharedPreferences("planner_storage", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPrefs)
}
