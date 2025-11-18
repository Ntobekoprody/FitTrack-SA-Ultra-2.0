package com.fittracksa.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Basic FCM service:
 * - onNewToken: saves token to users/{uid}.fcmToken if user signed in
 * - onMessageReceived: logs and creates a simple notification (optional)
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM new token: $token")
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message from: ${remoteMessage.from}")
        // If message has notification payload, show a basic notification
        remoteMessage.notification?.let { notif ->
            showNotification(notif.title ?: "FitTrack", notif.body ?: "")
        }
    }

    private fun saveTokenToFirestore(token: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        val firestore = FirebaseFirestore.getInstance()
        val map = mapOf("fcmToken" to token)
        firestore.collection("users").document(uid)
            .set(map, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "FCM token saved to user doc") }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to save fcm token: ${e.localizedMessage}") }
    }

    private fun showNotification(title: String, body: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fittrack_default"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "FitTrack", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(ch)
        }
        val n = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        nm.notify(1001, n)
    }
}
