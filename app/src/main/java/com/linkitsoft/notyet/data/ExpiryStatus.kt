package com.linkitsoft.notyet.data

import androidx.annotation.StringRes
import com.linkitsoft.notyet.R

enum class ExpiryStatus(
    @StringRes val labelResId: Int,
    val colorHex: Long,
) {
    EXPIRED(R.string.status_expired, 0xFFE53935),
    SOON(R.string.status_expiring_soon, 0xFFFB8C00),
    FINE(R.string.status_fine, 0xFF43A047);

    companion object {
        fun currentEpochDay(): Long {
            return System.currentTimeMillis() / 86_400_000L
        }

        fun fromExpiryDate(expiryEpochDay: Long, currentEpochDay: Long = currentEpochDay()): ExpiryStatus {
            val daysDiff = expiryEpochDay - currentEpochDay
            return when {
                daysDiff < 0 -> EXPIRED
                daysDiff <= 30 -> SOON
                else -> FINE
            }
        }
    }
}
