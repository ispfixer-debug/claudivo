package com.vito.driver

import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Driver FCM service - handles job offers and updates.
 * Per PLAN.md §20.1
 */
class VitoDriverFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val type = remoteMessage.data["type"]
        
        when (type) {
            "job_offer" -> {
                val jobId = remoteMessage.data["job_id"]
                val fare = remoteMessage.data["fare"]
                showJobOfferNotification(jobId, fare)
            }
            "job_accepted" -> {
                // Notify driver their offer was accepted
            }
            else -> {
                // Regular notification
            }
        }
    }

    private fun showJobOfferNotification(jobId: String?, fare: String?) {
        createNotificationChannel()
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("New Job Offer!")
            .setContentText("Fare: $${(fare?.toIntOrNull() ?: 0) / 100}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Job Offers",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Driver job offer notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update FCM token in database
    }

    companion object {
        const val CHANNEL_ID = "vito_driver_jobs"
        const val NOTIFICATION_ID = 1001
    }
}