package com.linkitsoft.notyet.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.linkitsoft.notyet.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val items = db.documentDao().getAllDocumentsDirect()
                    items.forEach { item ->
                        ReminderScheduler.scheduleRemindersForItem(context, item)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
