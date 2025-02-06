package com.example.stockscreener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.stockscreener.ui.theme.DefaultTheme

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
    NavHost(
        navController = navController,
        startDestination = Screen.StockList.route)
    {
        composable(navController,Screen.StockList)

    }

}

@Preview(showBackground = true)
@Composable
fun MainApplicationPreview() {
    MainApplication()
}