package com.example.stockscreener.network

import android.util.Log
import com.example.stockscreener.dao.CompanyOverviewDao
import com.example.stockscreener.dao.StockDao
import com.example.stockscreener.data.CompanyOverview
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.data.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

/**
 * getStockList() || getCompanyOverview()
 * 1) Check the API rate limit before calling API
 * 2) Use cache if API fails
 * 3) Save data into room DB
 * 4) Update last API call time
 * 5) Return parse API data else return data from room DB
 */
class Repository(
    private val stockDao: StockDao,
    private val companyOverviewDao: CompanyOverviewDao
) {

    private val rateLimit = ApiRateLimit()

    suspend fun getStockList(): List<Stock> {
        return withContext(Dispatchers.IO) {
            val cachedData = stockDao.getStocks().firstOrNull()
            try {
                if (!rateLimit.canMakeApiCall()) {
                    Log.e("API Rate Limit", "get stock request blocked due to rate limit")
                    return@withContext cachedData?: emptyList()
                }

                val response = RetrofitInstance.api.getStockList()
                Log.d("API Response", "Response code: ${response.code()}, body: ${response.body()}")
                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed with status code: ${response.code()}")
                    return@withContext cachedData ?: emptyList()
                }

                val body = response.body()?.string() ?: ""
                if (body.isEmpty()) {
                    Log.e("API Error", "Received empty response")
                    return@withContext cachedData ?: emptyList()
                }

                val apiStockList = parseCsv(body)
                val localStocks = cachedData ?: emptyList()

                val mergedStockList = apiStockList.map { apiStock ->
                    val localStock = localStocks.find { it.symbol == apiStock.symbol }
                    apiStock.copy(isFavorite = localStock?.isFavorite ?: false)
                }
                stockDao.insertStocks(mergedStockList)
                rateLimit.updateLastApiCall()
                mergedStockList

            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching stocks: ${e.message}")
                cachedData ?: emptyList()
            }
        }
    }

    /**
     * Parses csv file to list of object
     */
    private fun parseCsv(csv: String): List<Stock> {
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
                Log.d("API Response", "Response code: ${response.code()}, body: ${response.body()}")
                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed with status code: ${response.code()}")
                    return@withContext cacheData
                }

                val apiData = response.body()
                if (apiData == null || apiData.symbol.isBlank()) {
                    Log.e("API Error", "Invalid company overview response for $symbol")
                    return@withContext cacheData
                }

                val mapData = apiData.toEntity()
                Log.d("DB Insert", "Saving company overview: $mapData")
                companyOverviewDao.insertCompanyOverview(mapData)
                rateLimit.updateLastApiCall()
                mapData

            } catch (e: Exception) {
                Log.e("API Exception", "Error fetching company overview: ${e.message}")
                cacheData
            }
        }
    }

    private fun CompanyOverview.toEntity(): CompanyOverviewEntity {
        return CompanyOverviewEntity(
            symbol = symbol,
            assetType = assetType,
            name = name,
            description = description,
            cik = cik,
            exchange = exchange,
            currency = currency,
            country = country,
            sector = sector,
            industry = industry,
            address = address,
            fiscalYearEnd = fiscalYearEnd,
            latestQuarter = latestQuarter,
            marketCapitalization = marketCapitalization,
            ebitda = ebitda,
            peRatio = peRatio,
            pegRatio = pegRatio,
            bookValue = bookValue,
            dividendPerShare = dividendPerShare,
            dividendYield = dividendYield,
            eps = eps,
            revenuePerShareTTM = revenuePerShareTTM,
            profitMargin = profitMargin,
            operatingMarginTTM = operatingMarginTTM,
            returnOnAssetsTTM = returnOnAssetsTTM,
            returnOnEquityTTM = returnOnEquityTTM,
            revenueTTM = revenueTTM,
            grossProfitTTM = grossProfitTTM,
            dilutedEPSTTM = dilutedEPSTTM,
            quarterlyEarningsGrowthYOY = quarterlyEarningsGrowthYOY,
            quarterlyRevenueGrowthYOY = quarterlyRevenueGrowthYOY,
            analystTargetPrice = analystTargetPrice,
            analystRatingStrongBuy = analystRatingStrongBuy,
            analystRatingBuy = analystRatingBuy,
            analystRatingHold = analystRatingHold,
            analystRatingSell = analystRatingSell,
            analystRatingStrongSell = analystRatingStrongSell,
            trailingPE = trailingPE,
            forwardPE = forwardPE,
            priceToSalesRatioTTM = priceToSalesRatioTTM,
            priceToBookRatio = priceToBookRatio,
            evToRevenue = evToRevenue,
            evToEBITDA = evToEBITDA,
            beta = beta,
            fiftyTwoWeekHigh = fiftyTwoWeekHigh,
            fiftyTwoWeekLow = fiftyTwoWeekLow,
            fiftyDayMovingAverage = fiftyDayMovingAverage,
            twoHundredDayMovingAverage = twoHundredDayMovingAverage,
            sharesOutstanding = sharesOutstanding,
            dividendDate = dividendDate,
            exDividendDate = exDividendDate
        )
    }

    fun getCompanyOverviewFromDB(symbol: String): Flow<CompanyOverviewEntity?> {
        return companyOverviewDao.getCompanyOverview(symbol)
    }

}

