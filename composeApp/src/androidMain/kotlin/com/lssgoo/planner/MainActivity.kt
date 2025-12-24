package com.lssgoo.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lssgoo.planner.di.initializeSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize platform-specific settings
        initializeSettings(this)
        
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
