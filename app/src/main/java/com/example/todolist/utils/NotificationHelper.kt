package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID = "task_notifications"
    private const val PREFS_NAME = "notification_prefs"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

    // Sprawdź, czy powiadomienia są włączone (zarówno systemowo, jak i w ustawieniach aplikacji)
    fun isNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val systemNotificationsEnabled = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()
        val appNotificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) // Domyślnie włączone
        return systemNotificationsEnabled && appNotificationsEnabled
    }

    // Włącz powiadomienia
    fun enableNotifications(context: Context) {
        createNotificationChannel(context)
        saveNotificationState(context, true)
    }

    // Wyłącz powiadomienia
    fun disableNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        saveNotificationState(context, false)
    }

    // Zapisz stan powiadomień w SharedPreferences
    private fun saveNotificationState(context: Context, isEnabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, isEnabled)
            apply()
        }
    }

    // Tworzenie kanału powiadomień
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val name = "Task Notifications"
                val description = "Notifications for tasks"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    this.description = description
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // Pokazanie powiadomienia
    fun showNotification(context: Context, title: String, message: String) {
        if (isNotificationsEnabled(context)) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(context)) {
                if (areNotificationsEnabled()) {
                    notify(System.currentTimeMillis().toInt(), builder.build())
                }
            }
        }
    }
}