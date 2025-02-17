package com.example.stockscreener.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.stockscreener.data.Stock
import com.example.stockscreener.network.Repository
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.robolectric.annotation.Config
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.storage.StockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

@Config(manifest = Config.NONE)
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class StockViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StockViewModel
    private lateinit var repository: Repository
    private lateinit var application: Application
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Ensures coroutines run on test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Mock Application context
        application = ApplicationProvider.getApplicationContext()
        viewModel = StockViewModel(application)

        repository = mockk(relaxed = true)

        // Mock database operations to avoid main-thread access
        val databaseField = viewModel::class.java.getDeclaredField("database")
        databaseField.isAccessible = true

        val mockDatabase = mockk<StockDatabase>(relaxed = true)
        databaseField.set(viewModel, mockDatabase)

        //
        val repositoryField = viewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchStockTest() = runTest(testDispatcher){
        val stocks = listOf(
            Stock(
                symbol = "AAPL",
                name = "Apple Inc.",
                exchange = "NASDAQ",
                assetType = "Equity",
                ipoDate = "1980-12-12",
                status = "Active",
                isFavorite = false
            ),
            Stock(
                symbol="TESL",
                name="Simplify Volt TSLA Revolution ETF",
                exchange ="NYSE ARCA", assetType="ETF",
                ipoDate ="2020-12-29", status="Active",
                isFavorite = false)
        )

        coEvery { repository.getStockList() } returns stocks
        viewModel.fetchStocks()
        advanceUntilIdle()

        assertThat(viewModel.stocks.value).isEqualTo(stocks)
    }

    @Test
    fun searchStocksTest() = runTest(testDispatcher) {
        val keyword = "AAPL"

        val stocks = listOf(
            Stock(symbol = "AAPL", name = "Apple Inc.", exchange = "NASDAQ", assetType = "Equity", ipoDate = "1980-12-12", status = "Active",isFavorite = false),
            Stock(symbol ="AAPL1", name="Apple Test Corp.", exchange ="BATS", assetType="ETF", ipoDate ="2024-04-01", status="Active", isFavorite = false)
        )

        val stock = listOf(
            Stock("TESL", "Simplify Volt TSLA Revolution ETF", exchange ="NYSE ARCA", assetType="ETF", ipoDate ="2020-12-29", status="Active", isFavorite = false)
        )

        coEvery { repository.searchStock(keyword) } returns flowOf(stocks)
        viewModel.searchStocks(keyword)
        advanceUntilIdle()

        assertThat(viewModel.searchStocks.value).isEqualTo(stocks)
        assertThat(viewModel.isSearching.value).isTrue()
        assertThat(viewModel.searchStocks.value).isNotEqualTo(stock)

        viewModel.searchStocks("")
        advanceUntilIdle()

        assertThat(viewModel.searchStocks.value).isEmpty()
        assertThat(viewModel.isSearching.value).isFalse()
    }

    @Test
    fun getCompanyOverviewTest() = runTest(testDispatcher) {
        val companyOverview = CompanyOverviewEntity(
            symbol = "AAPL",
            name = "Apple Inc.",
            assetType = "Equity",
            marketCapitalization = "2.5T",
            dividendYield = "0.5%",
            fiftyTwoWeekHigh = "200.00",
            fiftyTwoWeekLow = "100.00"
        )

        coEvery { repository.getCompanyOverview("AAPL") } returns companyOverview
        viewModel.getCompanyOverview("AAPL")
        advanceUntilIdle()

        assertThat(viewModel.companyOverview.value).isEqualTo(companyOverview)
    }

    @Test
    fun fetchTimeSeriesMonthlyTest() = runTest(testDispatcher){
        val symbol = "AAPL"
        val latestPrice = 150.5f
        val timeSeriesData = listOf(
            "2024-01" to 140.0f,
            "2023-12" to 135.0f,
            "2023-11" to 130.0f
        )

        coEvery { repository.getTimeSeriesMonthly(symbol) } returns (latestPrice to timeSeriesData)
        viewModel.fetchTimeSeriesMonthly(symbol)
        advanceUntilIdle()

        assertThat(viewModel.currentStockPrice.value).isEqualTo(latestPrice)
        assertThat(viewModel.stockPricesChart.value).isEqualTo(timeSeriesData)
    }

}