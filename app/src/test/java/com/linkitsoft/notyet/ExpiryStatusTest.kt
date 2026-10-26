package com.linkitsoft.notyet

import com.linkitsoft.notyet.data.ExpiryStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpiryStatusTest {

    @Test
    fun fromExpiryDate_expired() {
        val currentDay = 20000L
        val expiryDay = 19999L
        val status = ExpiryStatus.fromExpiryDate(expiryDay, currentDay)
        assertEquals(ExpiryStatus.EXPIRED, status)
    }

    @Test
    fun fromExpiryDate_expiringSoon() {
        val currentDay = 20000L
        val expiryDay = 20015L
        val status = ExpiryStatus.fromExpiryDate(expiryDay, currentDay)
        assertEquals(ExpiryStatus.SOON, status)
    }

    @Test
    fun fromExpiryDate_fine() {
        val currentDay = 20000L
        val expiryDay = 20045L
        val status = ExpiryStatus.fromExpiryDate(expiryDay, currentDay)
        assertEquals(ExpiryStatus.FINE, status)
    }
}
