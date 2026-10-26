package com.linkitsoft.notyet.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.linkitsoft.notyet.data.AppDatabase
import com.linkitsoft.notyet.data.DocumentItem
import com.linkitsoft.notyet.data.ExpiryStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ExpiryWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getDatabase(context)
        val items = db.documentDao().getAllDocumentsDirect().take(4)

        provideContent {
            WidgetContent(items = items)
        }
    }

    @Composable
    private fun WidgetContent(items: List<DocumentItem>) {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1C1B1F))
                .padding(12.dp)
        ) {
            Text(
                text = "Upcoming Expiries",
                style = TextStyle(
                    color = ColorProvider(day = Color.White, night = Color.White),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            if (items.isEmpty()) {
                Text(
                    text = "No upcoming items",
                    style = TextStyle(
                        color = ColorProvider(day = Color.Gray, night = Color.Gray),
                        fontSize = 12.sp
                    )
                )
            } else {
                items.forEach { item ->
                    val status = ExpiryStatus.fromExpiryDate(item.expiryEpochDay)
                    val dateStr = dateFormat.format(item.expiryEpochDay * 86_400_000L)
                    val statusColor = Color(status.colorHex)

                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = item.title,
                            style = TextStyle(
                                color = ColorProvider(day = Color.White, night = Color.White),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = GlanceModifier.defaultWeight()
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            text = dateStr,
                            style = TextStyle(
                                color = ColorProvider(day = statusColor, night = statusColor),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

class ExpiryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ExpiryWidget()
}
