package com.vito.driver

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Vito Driver Application class.
 */
@HiltAndroidApp
class VitoDriverApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Firebase auto-initialized
    }
}