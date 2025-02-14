package com.example.stockscreener

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.stockscreener.stock.CompanyOverviewScreen
import com.example.stockscreener.stock.WatchListStockScreen
import com.example.stockscreener.stock.StockListScreen

sealed class Screen(val route: String, val content: @Composable (NavController, Bundle?) -> Unit) {
    data object StockList : Screen("StockList", { navController, _ -> StockListScreen(navController = navController) })
    data object WatchList : Screen("WatchList", { navController, _ -> WatchListStockScreen(navController = navController) })
    data object CompanyDetail : Screen("CompanyOverview/{symbol}", { navController, arguments ->
        val symbol = arguments?.getString("symbol") ?: error("Symbol is required.")
        CompanyOverviewScreen(navController = navController, symbol = symbol)
    })
}

fun NavGraphBuilder.composable(navController: NavController, screen: Screen) {
    composable(screen.route) {backStackEntry ->
        screen.content(navController, backStackEntry.arguments)
    }
}

/**
 * Navigate to the screen and remove all back stacks.
 */
fun NavController.navigate(screen: Screen) = navigate(screen.route)
