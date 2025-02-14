package com.example.stockscreener.network

import android.util.Log
import com.example.stockscreener.dao.CompanyOverviewDao
import com.example.stockscreener.dao.StockDao
import com.example.stockscreener.dao.TimeSeriesMonthlyDao
import com.example.stockscreener.data.CompanyOverview
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.data.Stock
import com.example.stockscreener.data.TimeSeriesMonthlyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

/**
 * getStockList() || getCompanyOverview() || getTimer
 * 1) Check the API rate limit before calling API
 * 2) Use cache if API fails
 * 3) Save data into room DB
 * 4) Update last API call time
 * 5) Return parse API data else return data from room DB
 */
class Repository(
    private val stockDao: StockDao,
    private val companyOverviewDao: CompanyOverviewDao,
    private val timeSeriesMonthlyDao : TimeSeriesMonthlyDao
) {

    private val rateLimit = ApiRateLimit()

    suspend fun getStockList(): List<Stock> {
        return withContext(Dispatchers.IO) {
            val cachedData = stockDao.getStocks().firstOrNull()
            var result: List<Stock> = cachedData ?: emptyList()
            try {
                if (!rateLimit.canMakeApiCall()) {
                    Log.e("API Rate Limit", "get stock request blocked due to rate limit")
                    return@withContext cachedData?: emptyList()
                }

                val response = RetrofitInstance.api.getStockList()
                Log.d("API Request", "URL: ${response.raw().request.url}")
                Log.d("API Response", "Response code: ${response.code()}")
                Log.d("API Raw Response", "Response body: ${response.body()}")

                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed with status code: ${response.code()}")
                    return@withContext cachedData ?: emptyList()
                } else {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        Log.e("API Error", "Response body is null")
                        result = cachedData ?: emptyList()
                    } else {
                        val apiStockList = parseCsv(responseBody)
                        val localStocks = cachedData ?: emptyList()

                        val mergedStockList = apiStockList.map { apiStock ->
                            val localStock = localStocks.find { it.symbol == apiStock.symbol }
                            apiStock.copy(isFavorite = localStock?.isFavorite ?: false)
                        }
                        Log.d("DB Insert", "Inserting ${apiStockList.size} stocks into database")
                        stockDao.insertStocks(mergedStockList)
                        rateLimit.updateLastApiCall()
                        result = mergedStockList
                    }
                }
            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching stocks: ${e.message}")
            }
            result
        }
    }

    /**
     * Parses csv file to list of object
     */
    private fun parseCsv(csv: String): List<Stock> {
        Log.d("CSV Data", "Raw CSV: $csv")
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

    fun searchStock(keyword: String): Flow<List<Stock>> {
        return stockDao.searchStocks(keyword)
    }

    suspend fun updateFavorite(symbol: String, isFavorite: Boolean) {
        stockDao.updateFavorite(symbol, isFavorite)
    }

    suspend fun getCompanyOverview(symbol: String): CompanyOverviewEntity?{
        return withContext(Dispatchers.IO){
            val cacheData = companyOverviewDao.getCompanyOverview(symbol).firstOrNull()
            try {
                if (!rateLimit.canMakeApiCall()) {
                    Log.e("API Rate Limit", "get company overview request blocked due to rate limit")
                    return@withContext cacheData
                }

                val response = RetrofitInstance.api.getCompanyOverview(symbol = symbol)
                Log.d("API Request", "URL: ${response.raw().request.url}")
                Log.d("API Response", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    Log.e("API Error Company Overview", "Failed with status code: ${response.code()}")
                    return@withContext cacheData
                }

                val apiData = response.body()
                Log.d("API Raw Response", "Response body: $apiData")

                if (apiData == null || apiData.symbol.isNullOrEmpty()) {
                    Log.w("API Warning", "Empty response for $symbol")
                    return@withContext cacheData
                }

                val mapData = apiData.toEntity()
                Log.d("DB Insert", "Saving company overview: $mapData")
                companyOverviewDao.insertCompanyOverview(mapData)

                rateLimit.updateLastApiCall()
                mapData

            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching company overview: ${e.message}")
                return@withContext cacheData
            }
        }
    }

    private fun CompanyOverview.toEntity(): CompanyOverviewEntity {
        return CompanyOverviewEntity(
            symbol = symbol,
            assetType = assetType,
            name = name,
            marketCapitalization = marketCapitalization,
            dividendYield = dividendYield,
            fiftyTwoWeekHigh = fiftyTwoWeekHigh,
            fiftyTwoWeekLow = fiftyTwoWeekLow
        )
    }

    fun getCompanyOverviewFromDB(symbol: String): Flow<CompanyOverviewEntity?> {
        return companyOverviewDao.getCompanyOverview(symbol)
    }

    suspend fun getTimeSeriesMonthly(symbol: String): Pair<Float, List<Pair<String, Float>>>  {
        return withContext(Dispatchers.IO) {
            val cachedData = timeSeriesMonthlyDao.getTimeSeries(symbol)
            val cachedPrice = cachedData.map { it.date to it.closePrice }
            var latestPrice = cachedData.firstOrNull()?.closePrice ?: 0f
            try {
                if (!rateLimit.canMakeApiCall()) {
                    Log.d("API Rate Limit Time Series Monthly", "get time series monthly request blocked due to rate limit")
                    return@withContext latestPrice to cachedPrice
                }

                val response = RetrofitInstance.api.getTimeSeriesMonthly(symbol = symbol)
                Log.d("API Request Time Series Monthly", "URL: ${response.raw().request.url}")
                Log.d("API Response Time Series Monthly", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed with status code: ${response.code()}")
                    return@withContext latestPrice to cachedPrice
                }

                val body = response.body()
                Log.d("API Response", "Response code : ${response.code()}, body: ${response.body()}")
                val timeSeries = body?.monthlyTimeSeries ?: return@withContext latestPrice to cachedPrice

                // Extract date and closing price, then sort by date
                val sortedPrices = timeSeries.mapNotNull { (date, entry) ->
                    entry.close.toFloatOrNull()?.let { closePrice ->
                        TimeSeriesMonthlyEntity(symbol = symbol, date = date, closePrice = closePrice)
                    }
                }.sortedByDescending { it.date }

                // Update latestPrice with the new API data
                latestPrice = sortedPrices.firstOrNull()?.closePrice ?: latestPrice

                timeSeriesMonthlyDao.deleteBySymbol(symbol) // Remove old data
                timeSeriesMonthlyDao.insertAll(sortedPrices)
                rateLimit.updateLastApiCall()

                return@withContext latestPrice to cachedPrice

            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching time series: ${e.message}")
                return@withContext latestPrice to cachedPrice
            }
        }
    }
}

