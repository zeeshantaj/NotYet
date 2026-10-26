package com.linkitsoft.notyet.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.linkitsoft.notyet.data.DocumentItem
import com.linkitsoft.notyet.data.ExpiryStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object ReminderScheduler {

    fun scheduleRemindersForItem(context: Context, item: DocumentItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val currentEpochDay = ExpiryStatus.currentEpochDay()

        cancelRemindersForItem(context, item.id)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val expiryMillis = item.expiryEpochDay * 86_400_000L
        val expiryDateString = dateFormat.format(expiryMillis)

        item.reminderDays.forEachIndexed { index, daysBefore ->
            val targetEpochDay = item.expiryEpochDay - daysBefore
            if (targetEpochDay >= currentEpochDay) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = targetEpochDay * 86_400_000L
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    val intent = Intent(context, NotificationReceiver::class.java).apply {
                        putExtra(NotificationReceiver.EXTRA_ITEM_ID, item.id)
                        putExtra(NotificationReceiver.EXTRA_TITLE, item.title)
                        putExtra(NotificationReceiver.EXTRA_EXPIRY_DATE, expiryDateString)
                        putExtra(NotificationReceiver.EXTRA_DAYS_LEFT, daysBefore)
                    }

                    val requestCode = (item.id * 100 + index).toInt()
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        } else {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    } catch (e: SecurityException) {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                }
            }
        }
    }

    fun cancelRemindersForItem(context: Context, itemId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (index in 0..10) {
            val requestCode = (itemId * 100 + index).toInt()
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }
}
