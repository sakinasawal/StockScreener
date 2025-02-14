package com.example.stockscreener.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_series_monthly")
data class TimeSeriesMonthlyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val date: String,
    val closePrice: Float
)
