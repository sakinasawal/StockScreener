package com.example.stockscreener.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockscreener.data.Stock
import com.example.stockscreener.network.Repository
import com.example.stockscreener.storage.StockDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {
    private val stockDao = StockDatabase.getDatabase(application).stockDao()
    private val repository = Repository(stockDao)

    private val listStocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> get() = listStocks

    init {
        viewModelScope.launch{
            repository.getStocksDB().collect{
                listStocks.value = it
            }
        }
    }

    fun fetchStocks() {
        viewModelScope.launch {
            try{
                val response = repository.getStockList()
                listStocks.value = response
                Log.d("API Response Size", response.size.toString())
                Log.d("API Response", response.toString())
            } catch ( e:Exception){
                Log.e("API Error", "Failed to fetch data: ${e.message}")
            }
        }
    }

}