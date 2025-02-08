package com.example.stockscreener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockscreener.data.Stock
import com.example.stockscreener.network.Repository
import com.example.stockscreener.storage.StockDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {
    private val stockDao = StockDatabase.getDatabase(application).stockDao()
    private val repository = Repository(stockDao)

    private val listStocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> get() = listStocks

    private val searchListStocks = MutableStateFlow<List<Stock>>(emptyList())
    val searchStocks: StateFlow<List<Stock>> get() = searchListStocks

    private val isSearchStock = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = isSearchStock

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
}