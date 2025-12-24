package com.lssgoo.planner.di

import com.russhwolf.settings.Settings

/**
 * Platform-specific Settings factory
 */
expect fun createSettings(): Settings
