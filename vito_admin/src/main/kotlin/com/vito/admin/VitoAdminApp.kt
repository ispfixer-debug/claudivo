package com.vito.admin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Vito Admin Application class.
 */
@HiltAndroidApp
class VitoAdminApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Firebase auto-initialized
    }
}