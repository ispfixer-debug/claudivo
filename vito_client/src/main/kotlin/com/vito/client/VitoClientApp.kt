package com.vito.client

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Vito Client Application class.
 * Per PLAN.md §20 - initializes Firebase, Analytics, Crashlytics
 */
@HiltAndroidApp
class VitoClientApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Firebase is auto-initialized via google-services.json
        // Analytics and Crashlytics start automatically
    }
}