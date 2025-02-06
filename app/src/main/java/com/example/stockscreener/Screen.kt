package com.example.stockscreener

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.stockscreener.stock.StockListScreen

sealed class Screen(val route: String, val content: @Composable (NavController, Bundle?) -> Unit) {
    data object StockList : Screen("StockList", { navController, _ -> StockListScreen(navController = navController) })

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