package com.example.stockscreener.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockscreener.R
import com.example.stockscreener.TextLabel
import com.example.stockscreener.*
import com.example.stockscreener.data.Stock
import com.example.stockscreener.ui.theme.AppTypography
import com.example.stockscreener.ui.theme.*
import com.example.stockscreener.viewmodel.StockViewModel

@Composable
fun StockListScreen(navController: NavController? = null) {

    val viewModel: StockViewModel = viewModel()
    val stocks by viewModel.stocks.collectAsState(initial = emptyList())
    val searchStocksResults by viewModel.searchStocks.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var searchStock by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.fetchStocks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing_20)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.spacedBy(spacing_20),
        horizontalAlignment = Alignment.Start
    ) {
        TextLabel(
            text = R.string.stock_list,
            typographyStyle = AppTypography.titleLarge
        )

        InputTextField(
            value = searchStock,
            onValueChange = {
                searchStock = it
                viewModel.searchStocks(it)
            },
            placeholder = stringResource(id = R.string.search),
            onClear = {
                searchStock = ""
                viewModel.searchStocks("")
            }
        )

        // Display list of stock
        LazyColumn {
            val displaySearchStock = if (isSearching) searchStocksResults else stocks
            items(displaySearchStock) { stock ->
                StockItem(
                    stock = stock,
                    onFavoriteClick = { selectedStock ->
                        viewModel.toggleFavorite(selectedStock)
                    },
                    onClick = {
                        navController?.navigate("CompanyOverview/${stock.symbol}")
                    }
                )
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock, onFavoriteClick: (Stock) -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing_8)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(spacing_4)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing_16),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Symbol: ${stock.symbol}")
                Text(text = "Name: ${stock.name}")
                Text(text = "Exchange: ${stock.exchange}")
                Text(text = "ipoDate: ${stock.ipoDate}")
                Text(text = "Status: ${stock.status}")
            }
            IconButton(
                onClick = { onFavoriteClick(stock) }
            ){
                Icon(imageVector = if (stock.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Watch List",
                    tint = if (stock.isFavorite) Purple40 else Color.LightGray)
            }
        }
    }
}

@Preview
@Composable
private fun StockListScreenPreview(){
    StockListScreen()
}