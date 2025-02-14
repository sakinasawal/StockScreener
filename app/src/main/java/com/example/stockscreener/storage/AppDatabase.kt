package com.example.stockscreener.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockscreener.dao.CompanyOverviewDao
import com.example.stockscreener.dao.StockDao
import com.example.stockscreener.dao.TimeSeriesMonthlyDao
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.data.Stock
import com.example.stockscreener.data.TimeSeriesMonthlyEntity

@Database(entities = [Stock::class, CompanyOverviewEntity::class, TimeSeriesMonthlyEntity::class], version = 1, exportSchema = false)
abstract class StockDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun companyOverviewDao() :  CompanyOverviewDao
    abstract fun timeSeriesMonthlyDao() : TimeSeriesMonthlyDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
