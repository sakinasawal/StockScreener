package com.example.stockscreener.network

import android.content.Context

/**
 * To prevent API from reaching rate limit
 */
class ApiRateLimit {
    private val requestTimestamps = mutableListOf<Long>()
    private val maxRequests = 5
    private val timeWindowMillis = 60_000L // 1 minute

    fun canMakeApiCall(): Boolean {
        val currentTime = System.currentTimeMillis()

        // Remove old timestamps outside the 1-minute window
        requestTimestamps.removeAll { it < currentTime - timeWindowMillis }

        return requestTimestamps.size < maxRequests
    }

    fun updateLastApiCall() {
        requestTimestamps.add(System.currentTimeMillis())
    }
}

