package com.customerdynamics.sdktest.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nice.cxonechat.ChatInstanceProvider
import com.customerdynamics.sdktest.MainActivity
import androidx.core.net.toUri

internal class PushListenerService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val chat = ChatInstanceProvider.get().chat
        if (chat == null) {
            // There is no existing instance of Chat SDK, no need to update.
            Log.v("LOG", "No chat instance present, token not passed")
            return
        }
        chat.setDeviceToken(token) // Update the instance with current token
        Log.d("LOG", "Registering push notifications token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Minimal local notification for any incoming FCM (data-only or notification+data)
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["pinpoint.notification.title"]
            ?: "TITLE"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["pinpoint.notification.body"]
            ?: "MESSAGE"

        val channelId = "chat_default"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "General",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        // If there's a deep link in the notification, handle it.
        val deepLinkUri = remoteMessage.data["pinpoint.deeplink"] ?: ""
        val intent = if (deepLinkUri.isNotEmpty()) {
            Log.d("LOG", "Received deep link: $deepLinkUri")
            Intent(Intent.ACTION_VIEW, deepLinkUri.toUri())
        } else {
            Log.d("LOG", "No deep link received. Navigating to MainActivity.")
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon if desired
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        nm.notify(id, notification)
    }
}