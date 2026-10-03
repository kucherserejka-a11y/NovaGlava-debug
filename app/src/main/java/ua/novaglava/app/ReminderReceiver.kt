package ua.novaglava.app

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

private const val CHANNEL_ID = "nova_glava_daily"
private const val ACTION_MORNING = "ua.novaglava.app.REMINDER_MORNING"
private const val ACTION_EVENING = "ua.novaglava.app.REMINDER_EVENING"

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val morning = intent.action == ACTION_MORNING
        val prefs = context.getSharedPreferences("nova_glava", Context.MODE_PRIVATE)
        val dayStarted = prefs.getBoolean("day_started", false)
        if (morning && dayStarted) return
        if (!morning && !dayStarted) return
        val chapter = prefs.getString("chapter_title", "Нова глава") ?: "Нова глава"
        val day = prefs.getInt("day", 1)
        val frontline = prefs.getString("chapter_frontline", "твоя Передова") ?: "твоя Передова"
        val title = if (morning) "Нова глава · День $day" else "Check-out · День $day"
        val body = if (morning) {
            "$chapter: роль → стан → SWITCH → Передова. Головний фокус: $frontline"
        } else {
            "Зафіксуй Evidence: що зробив → що змінилося в реальності."
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Щоденні нагадування",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Ранковий запуск дня та вечірній check-out"
                }
            )
        }

        val openApp = PendingIntent.getActivity(
            context,
            if (morning) 501 else 502,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(Notification.BigTextStyle().bigText(body))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        try {
            manager.notify(if (morning) 1001 else 1002, notification)
        } catch (_: SecurityException) {
            // Android 13+: користувач міг не надати дозвіл на сповіщення.
        }
    }
}

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val prefs = context.getSharedPreferences("nova_glava", Context.MODE_PRIVATE)
        applyReminderSettings(
            context = context,
            morningEnabled = prefs.getBoolean("reminder_morning_enabled", false),
            eveningEnabled = prefs.getBoolean("reminder_evening_enabled", false),
            morningTime = prefs.getString("reminder_morning_time", "08:30") ?: "08:30",
            eveningTime = prefs.getString("reminder_evening_time", "21:00") ?: "21:00"
        )
    }
}

fun applyReminderSettings(
    context: Context,
    morningEnabled: Boolean,
    eveningEnabled: Boolean,
    morningTime: String,
    eveningTime: String
) {
    if (morningEnabled) scheduleDailyReminder(context, true, morningTime) else cancelDailyReminder(context, true)
    if (eveningEnabled) scheduleDailyReminder(context, false, eveningTime) else cancelDailyReminder(context, false)
}

private fun scheduleDailyReminder(context: Context, morning: Boolean, time: String) {
    val (hour, minute) = parseReminderTime(time) ?: return
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = reminderPendingIntent(context, morning)
    val now = Calendar.getInstance()
    val next = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        next.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

private fun cancelDailyReminder(context: Context, morning: Boolean) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(reminderPendingIntent(context, morning))
}

private fun reminderPendingIntent(context: Context, morning: Boolean): PendingIntent {
    val action = if (morning) ACTION_MORNING else ACTION_EVENING
    return PendingIntent.getBroadcast(
        context,
        if (morning) 601 else 602,
        Intent(context, ReminderReceiver::class.java).setAction(action),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun parseReminderTime(value: String): Pair<Int, Int>? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null
    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return hour to minute
}
