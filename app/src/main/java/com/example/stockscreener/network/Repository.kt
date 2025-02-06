package com.example.stockscreener.network

import android.util.Log
import com.example.stockscreener.dao.StockDao
import com.example.stockscreener.data.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class Repository(private val stockDao: StockDao) {

    suspend fun getStockList(): List<Stock> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getStockList()
                val stockList = parseCsv(response)
                // Save data into Room DB
                stockDao.insertStocks(stockList)

                // Return parsed API data
                stockList
            } catch (e: Exception) {
                // Return cached data if API fails
                stockDao.getStocks().firstOrNull() ?: emptyList()
            }
        }
    }

    /**
     * Parses csv file to list of object
     */
    private fun parseCsv(csv: String): List<Stock> {
        Log.d("CSV Data", csv)
        val lines = csv.split("\n").drop(1) // Remove header
        return lines.mapNotNull { line ->
            val values = line.split(",")
            if (values.size >= 6) {
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
}

