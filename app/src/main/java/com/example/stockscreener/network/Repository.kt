package com.example.stockscreener.network

import android.util.Log
import com.example.stockscreener.dao.StockDao
import com.example.stockscreener.data.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class Repository(private val stockDao: StockDao) {

    private val rateLimit = ApiRateLimit()

    /**
     * 1) Check the API rate limit before calling API
     * 2) Use cache if API fails
     * 3) Save data into room DB
     * 4) Update last API call time
     * 5) Return parse API data
     */
    suspend fun getStockList(): List<Stock> {
        return withContext(Dispatchers.IO) {
            try {
                val cachedData = stockDao.getStocks().firstOrNull()
                if (!rateLimit.canMakeApiCall()) {
                    Log.e("API Rate Limit", "get stock request blocked due to rate limit")
                    return@withContext cachedData?: emptyList()
                }

                val response = RetrofitInstance.api.getStockList()
                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed with status code: ${response.code()}")
                    return@withContext cachedData ?: emptyList()
                }

                val body = response.body()
                if (body.isNullOrEmpty()) {
                    Log.e("API Error", "Received empty response")
                    return@withContext cachedData ?: emptyList()
                }

                val apiStockList = parseCsv(body)
                val localStocks = stockDao.getStocks().firstOrNull() ?: emptyList()

                val mergedStockList = apiStockList.map { apiStock ->
                    val localStock = localStocks.find { it.symbol == apiStock.symbol }
                    apiStock.copy(isFavorite = localStock?.isFavorite ?: false)
                }
                stockDao.insertStocks(mergedStockList)
                rateLimit.updateLastApiCall()
                mergedStockList

            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching stocks: ${e.message}")
                // Return cached data if API fails
                stockDao.getStocks().firstOrNull() ?: emptyList()
            }
        }
    }

    /**
     * Parses csv file to list of object
     */
    private fun parseCsv(csv: String): List<Stock> {
        Log.d("API Raw Response", csv)
        val lines = csv.split("\n").drop(1) // Remove header
        return lines.mapNotNull { line ->
            val values = line.split(",")
            if (values.size >= 7) {
                Stock(
                    symbol = values[0],
                    name = values[1],
                    exchange = values[2],
                    assetType = values[3],
                    ipoDate = values[4],
                    status = values[6]
                )
            } else {
                null
            }
        }
    }

    fun getStocksDB(): Flow<List<Stock>> {
        return stockDao.getStocks()
    }

    fun searchStock(keyword: String): Flow<List<Stock>> {
        return stockDao.searchStocks(keyword)
    }

    suspend fun updateFavorite(symbol: String, isFavorite: Boolean) {
        stockDao.updateFavorite(symbol, isFavorite)
    }

}

