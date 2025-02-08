package com.example.stockscreener.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getStockList(
        @Query("function") function: String = "LISTING_STATUS",
        @Query("apikey") apiKey: String = "HYM9GGTGCZALWXNX"
    ): Response<String>

}

