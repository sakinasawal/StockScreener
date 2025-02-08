package com.example.stockscreener.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockscreener.data.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<Stock>)

    @Query("SELECT * FROM stocks")
    fun getStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM stocks WHERE symbol LIKE '%' || :keyword || '%'")
    fun searchStocks(keyword: String): Flow<List<Stock>>

    @Query("UPDATE stocks SET isFavorite = :isFavorite WHERE symbol = :symbol")
    suspend fun updateFavorite(symbol: String, isFavorite: Boolean)

}