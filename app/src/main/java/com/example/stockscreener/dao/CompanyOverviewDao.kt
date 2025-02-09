package com.example.stockscreener.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockscreener.data.CompanyOverviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyOverviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyOverview(companyOverview: CompanyOverviewEntity)

    @Query("SELECT * FROM company_overview WHERE symbol = :symbol LIMIT 1")
    fun getCompanyOverview(symbol: String): Flow<CompanyOverviewEntity?>
}