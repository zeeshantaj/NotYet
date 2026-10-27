package com.linkitsoft.notyet.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("notyet_prefs", Context.MODE_PRIVATE)

    var defaultReminderDays: List<Int>
        get() {
            val str = prefs.getString(KEY_DEFAULT_REMINDERS, "90,30,7,1") ?: "90,30,7,1"
            return str.split(",").mapNotNull { it.trim().toIntOrNull() }
        }
        set(value) {
            prefs.edit().putString(KEY_DEFAULT_REMINDERS, value.joinToString(",")).apply()
        }

    var notificationHour: Int
        get() = prefs.getInt(KEY_NOTIF_HOUR, 9)
        set(value) {
            prefs.edit().putInt(KEY_NOTIF_HOUR, value).apply()
        }

    var notificationMinute: Int
        get() = prefs.getInt(KEY_NOTIF_MINUTE, 0)
        set(value) {
            prefs.edit().putInt(KEY_NOTIF_MINUTE, value).apply()
        }

    companion object {
        private const val KEY_DEFAULT_REMINDERS = "default_reminders"
        private const val KEY_NOTIF_HOUR = "notif_hour"
        private const val KEY_NOTIF_MINUTE = "notif_minute"
    }
}
