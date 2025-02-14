package com.example.stockscreener.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockscreener.viewmodel.StockViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.stockscreener.R
import com.example.stockscreener.TextLabel
import com.example.stockscreener.spacing_20
import com.example.stockscreener.ui.theme.AppTypography

@Composable
fun WatchListStockScreen(navController: NavController? = null){
    val viewModel: StockViewModel = viewModel()
    val stocks by viewModel.stocks.collectAsState()
    val favoriteStocks = stocks.filter { it.isFavorite }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing_20),
        verticalArrangement = Arrangement.spacedBy(spacing_20),
        horizontalAlignment = Alignment.Start
    ){
        TextLabel(
            text = R.string.watch_list,
            typographyStyle = AppTypography.titleLarge
        )

        if (favoriteStocks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = stringResource(R.string.no_watch_list),
                    style = AppTypography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(favoriteStocks) { stock ->
                    val latestPriceAndChange by viewModel.getLatestPriceAndChange(stock.symbol).collectAsState(initial = 0f to null)

                    StockItem(
                        stock = stock,
                        onFavoriteClick = { selectedStock ->
                            viewModel.toggleFavorite(selectedStock)
                        },
                        onClick = {
                            navController?.navigate("CompanyOverview/${stock.symbol}")
                        },
                        latestPrice = latestPriceAndChange.first,
                        percentageChange = latestPriceAndChange.second
                    )
                }
            }
        }
    }
}