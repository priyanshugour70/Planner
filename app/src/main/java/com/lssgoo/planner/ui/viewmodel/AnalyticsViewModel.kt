package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.data.analytics.AnalyticsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Analytics feature
 */
class AnalyticsViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val analyticsManager = AnalyticsManager(storageManager)
    
    private val _analyticsData = MutableStateFlow<AnalyticsData?>(null)
    val analyticsData: StateFlow<AnalyticsData?> = _analyticsData.asStateFlow()
    
    init {
        refreshAnalytics()
    }
    
    fun refreshAnalytics() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                _analyticsData.value = analyticsManager.generateComprehensiveReport()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
