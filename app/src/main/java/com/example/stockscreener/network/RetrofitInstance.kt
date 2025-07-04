package com.example.stockscreener.network

import com.example.stockscreener.util.ApiKeyProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://www.alphavantage.co/"
    val API_KEY = ApiKeyProvider.getApiKey()

    private val client = OkHttpClient.Builder()

        .addInterceptor{ chain ->
            val original = chain.request()
            val originalUrl = original.url

            val url = originalUrl.newBuilder()
                .addQueryParameter("apiKey", API_KEY)
                .build()

            val requestBuilder = original.newBuilder()
                .url(url)
                .build()

            chain.proceed(requestBuilder)
        }

        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}