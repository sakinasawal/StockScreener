package com.example.stockscreener.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockscreener.R
import com.example.stockscreener.TextLabel
import com.example.stockscreener.data.CompanyOverviewEntity
import com.example.stockscreener.spacing_20
import com.example.stockscreener.spacing_4
import com.example.stockscreener.spacing_8
import com.example.stockscreener.ui.theme.AppTypography
import com.example.stockscreener.viewmodel.StockViewModel

@Composable
fun CompanyOverviewScreen(
    symbol: String,
) {
    val viewModel: StockViewModel = viewModel()
    val companyOverview by viewModel.companyOverview.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCompanyOverview(symbol)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing_8),
        verticalArrangement = Arrangement.spacedBy(spacing_20),
        horizontalAlignment = Alignment.Start
    ){
        TextLabel(
            text = R.string.company_overview,
            typographyStyle = AppTypography.titleLarge
        )

        when {
            companyOverview == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
            else -> CompanyOverviewContent(company = companyOverview!!)
        }
    }

}

@Composable
fun CompanyOverviewContent(company : CompanyOverviewEntity){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing_8),
        elevation = CardDefaults.cardElevation(spacing_4)
    ){
        val companyDetails = listOf(
            "symbol" to company.symbol,
            "asset type" to company.assetType,
            "name" to company.name,
            "Market Capitalization" to company.marketCapitalization,
            "Dividend yield" to company.dividendYield,
            "52-WeekHigh" to company.fiftyTwoWeekHigh,
            "52-WeekLow" to company.fiftyTwoWeekLow,
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()) // Enable scrolling
                .padding(spacing_8)
        ) {
            Column {
                companyDetails.forEach { (label, value) ->
                    Text(text = "$label : $value")
                }

                Spacer(modifier = Modifier.height(spacing_20))

                StockChartScreen(symbol = company.symbol)
            }
        }
    }
}