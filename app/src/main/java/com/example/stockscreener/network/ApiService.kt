package com.example.stockscreener.network

import com.example.stockscreener.data.CompanyOverview
import com.example.stockscreener.data.TimeSeriesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getStockList(
        @Query("function") function: String = "LISTING_STATUS"
    ): Response<String>

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String
    ): Response<CompanyOverview>

    @GET("query")
    suspend fun getTimeSeriesMonthly(
        @Query("function") function: String = "TIME_SERIES_MONTHLY",
        @Query("symbol") symbol: String
    ): Response<TimeSeriesResponse>

}

