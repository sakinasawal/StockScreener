package com.example.stockscreener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.stockscreener.stock.BottomNavBar
import com.example.stockscreener.ui.theme.DefaultTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import com.example.stockscreener.stock.FavouriteListStockScreen
import com.example.stockscreener.stock.StockListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DefaultTheme {
                MainApplication()
            }
        }
    }
}

@Composable
fun MainApplication() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.StockList.route,
            modifier = Modifier.padding(paddingValues))
        {
            composable(navController, Screen.StockList)
            composable(navController, Screen.Favorites)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainApplicationPreview() {
    MainApplication()
}