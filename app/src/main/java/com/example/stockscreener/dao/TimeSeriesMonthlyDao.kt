package com.example.stockscreener.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockscreener.data.TimeSeriesMonthlyEntity

@Dao
interface TimeSeriesMonthlyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<TimeSeriesMonthlyEntity>)

    @Query("DELETE FROM time_series_monthly WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)

    @Query("SELECT * FROM time_series_monthly WHERE symbol = :symbol ORDER BY date DESC")
    suspend fun getTimeSeries(symbol: String): List<TimeSeriesMonthlyEntity>
}