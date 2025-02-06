package com.example.stockscreener.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getStockList(
        @Query("function") function: String = "LISTING_STATUS",
        @Query("apikey") apiKey: String = "ZGUFQASN7NQR3NE4"
    ): String

}

