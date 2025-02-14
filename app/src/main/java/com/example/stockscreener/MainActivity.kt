package com.example.stockscreener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.stockscreener.stock.BottomNavBar
import com.example.stockscreener.ui.theme.DefaultTheme
import androidx.compose.ui.Modifier
import com.example.stockscreener.network.NetworkMonitor

class MainActivity : ComponentActivity() {
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(this)
        networkMonitor.register()

        enableEdgeToEdge()
        setContent {
            val isConnected by networkMonitor.isConnectedNetwork.collectAsState()

            DefaultTheme {
                MainApplication(isConnected)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
    }
}

@Composable
fun MainApplication(isConnected : Boolean) {
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
            composable(navController, Screen.WatchList)
            composable(navController, Screen.CompanyDetail)
        }

        if (!isConnected){
            NoNetworkInternetScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainApplicationPreview() {
    MainApplication(isConnected = true)
}