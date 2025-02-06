package com.example.stockscreener.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockscreener.R
import com.example.stockscreener.TextLabel
import com.example.stockscreener.*
import com.example.stockscreener.data.Stock
import com.example.stockscreener.ui.theme.AppTypography
import com.example.stockscreener.viewmodel.StockViewModel

@Composable
fun StockListScreen(navController: NavController? = null) {

    val viewModel: StockViewModel = viewModel()
    val stocks by viewModel.stocks.collectAsState(initial = emptyList())
    var searchStock by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchStocks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing_20),
        verticalArrangement = Arrangement.spacedBy(spacing_20),
        horizontalAlignment = Alignment.Start
    ) {
        TextLabel(
            text = R.string.stock_list,
            typographyStyle = AppTypography.titleLarge
        )

        InputTextField(
            value = searchStock,
            onValueChange = { searchStock = it },
            placeholder = stringResource(id = R.string.search),
            onClear = { searchStock = ""}
        )

        // Display list of stock
        LazyColumn {
            items(stocks) { stock ->
                StockItem(stock)
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing_8),
        elevation = CardDefaults.cardElevation(spacing_4)
    ) {
        Column(modifier = Modifier.padding(spacing_16)) {
            Text(text = stock.symbol)
            Text(text = stock.name)
            Text(text = stock.exchange)
            Text(text = stock.ipoDate)
            Text(text = stock.status)
        }
    }
}


@Preview
@Composable
private fun StockListScreenPreview(){
    StockListScreen()
}