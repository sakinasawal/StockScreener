package com.example.stockscreener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.data.Stock
import com.example.stockscreener.network.Repository
import com.example.stockscreener.storage.StockDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {
    private val database = StockDatabase.getDatabase(application)
    private val repository = Repository(database.stockDao(), database.companyOverviewDao())

    private val listStocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> get() = listStocks

    private val searchListStocks = MutableStateFlow<List<Stock>>(emptyList())
    val searchStocks: StateFlow<List<Stock>> get() = searchListStocks

    private val isSearchStock = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = isSearchStock

    private val companyOverviewData = MutableStateFlow<CompanyOverviewEntity?>(null)
    val companyOverview: StateFlow<CompanyOverviewEntity?> = companyOverviewData

    private val chartStockPrices = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val stockPrices: StateFlow<List<Pair<String, Float>>> = chartStockPrices

    private val isLoading = MutableStateFlow(false)
    val isTimeSeriesLoading: StateFlow<Boolean> = isLoading

    init {
        viewModelScope.launch{
            repository.getStocksDB().collect{
                listStocks.value = it
            }
        }
    }

    fun fetchStocks() {
        viewModelScope.launch {
            repository.getStockList()
        }
    }

    fun searchStocks(keyword: String) {
        viewModelScope.launch {
            if (keyword.isNotEmpty()){
                isSearchStock.value = true
                val localSearch = repository.searchStock(keyword).first()
                searchListStocks.value = localSearch.ifEmpty {
                    repository.getStockList().filter {
                        it.symbol.contains(keyword, ignoreCase = true)
                    }
                }
            } else {
                isSearchStock.value = false
                searchListStocks.value = emptyList()
            }
        }
    }

    fun toggleFavorite(stock: Stock) {
        viewModelScope.launch {
            val newFavoriteStatus = !stock.isFavorite
            repository.updateFavorite(stock.symbol, newFavoriteStatus)

            listStocks.value = listStocks.value.map {
                if (it.symbol == stock.symbol) it.copy(isFavorite = newFavoriteStatus) else it
            }
        }
    }

    fun getCompanyOverview(symbol: String) {
        viewModelScope.launch {
            val apiResponse = repository.getCompanyOverview(symbol)
            if ( apiResponse != null){
                companyOverviewData.value = apiResponse
            } else {
                repository.getCompanyOverviewFromDB(symbol).collect {
                    companyOverviewData.value = it
                }
            }
        }
    }

    fun fetchTimeSeriesMonthly(symbol: String) {
        viewModelScope.launch {
            val apiData = repository.getTimeSeriesMonthly(symbol)
            chartStockPrices.value = apiData
        }
    }
}