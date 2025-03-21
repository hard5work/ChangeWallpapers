package com.xdroid.app.changewallpaper.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.ui.activity.MainActivity
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            DebugMode.e("data from notification ${it.title} ${it.body}")
            showNotification(
                context = this,
                title = it.title ?: "Notification",
                message = it.body ?: "You have a new message."
            )
        }
//        // Check if the message contains data and use it to show notification
//        remoteMessage.data.let { data ->
//            DebugMode.e("Notification ->>>>> ${data}" )
//            val title = data["title"] ?: "Notification"
//            val message = data["message"] ?: "You have a new message."
//            showNotification(this, title, message)
//        }
    }

    override fun onNewToken(token: String) {
        // Handle the new token if needed
        // Send this token to your server if required for targeted notifications
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "firebase_notifications_channel"

        // Create an Intent to open the app when the notification is tapped
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("notification_title", title)
        intent.putExtra("notification_message", message)
        DebugMode.e("Notification $title , $message")
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the default notification sound URI
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Create Notification Channel (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Firebase Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for Firebase notifications"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
