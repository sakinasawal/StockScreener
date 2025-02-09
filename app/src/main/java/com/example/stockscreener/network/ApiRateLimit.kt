package com.example.stockscreener.network

import android.content.Context
import android.util.Log

/**
 * To prevent API from reaching rate limit in one minute
 */
class ApiRateLimit {
    private val requestTimestamps = mutableListOf<Long>()
    private val maxRequests = 5
    private val timeWindowMillis = 60000L // 1 minute

    fun canMakeApiCall(): Boolean {
        val currentTime = System.currentTimeMillis()  // Get the current timestamp

        // Remove old timestamps that are older than 1 minute
        requestTimestamps.removeAll { it < currentTime - timeWindowMillis }

        // Check if we have made less than 5 requests in the last 60 seconds
        val canCall = requestTimestamps.size < maxRequests
        return canCall
    }

    fun updateLastApiCall() {
        val currentTime = System.currentTimeMillis()
        requestTimestamps.add(currentTime)
        Log.d("RateLimit Debug", "API called. Total requests stored: ${requestTimestamps.size}")
    }
}

