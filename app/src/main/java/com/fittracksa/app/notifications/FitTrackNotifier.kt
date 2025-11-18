package com.fittracksa.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fittracksa.app.MainActivity
import com.fittracksa.app.R
import com.fittracksa.app.data.preferences.SettingsRepository
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FitTrackNotifier(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val notificationManager = NotificationManagerCompat.from(context)
    private val notificationId = AtomicInteger(4000)

    init {
        ensureChannel()
    }

    fun post(event: Event) {
        appScope.launch {
            if (!settingsRepository.areNotificationsEnabled()) return@launch
            val (title, message) = contentFor(event)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent())
                .build()
            notificationManager.notify(notificationId.getAndIncrement(), notification)
        }
    }

    private fun contentFor(event: Event): Pair<String, String> = when (event) {
        is Event.LoggedIn ->
            context.getString(R.string.app_name) to "Welcome back, ${event.userName}! You're logged in."
        is Event.ActivitySaved ->
            "Activity logged" to "${event.type} for ${event.duration} min saved offline and will sync soon."
        is Event.MealSaved ->
            "Meal saved" to "${event.description} (${event.calories} kcal) stored and queued for sync."
        is Event.SignedOut ->
            "Signed out" to "See you soon, ${event.userName}. You're safely logged out."
    }

    private fun mainPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    sealed class Event {
        data class LoggedIn(val userName: String) : Event()
        data class SignedOut(val userName: String) : Event()
        data class ActivitySaved(val type: String, val duration: Int) : Event()
        data class MealSaved(val description: String, val calories: Int) : Event()
    }

    companion object {
        private const val CHANNEL_ID = "fittrack_alerts"
    }
}
