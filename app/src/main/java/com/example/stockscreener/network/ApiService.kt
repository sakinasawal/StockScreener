package com.example.stockscreener.network

import com.example.stockscreener.data.CompanyOverview
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getStockList(
        @Query("function") function: String = "LISTING_STATUS",
        @Query("apikey") apiKey: String = "HYM9GGTGCZALWXNX"
    ): Response<ResponseBody>

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = "HYM9GGTGCZALWXNX"
    ): Response<CompanyOverview>

}

