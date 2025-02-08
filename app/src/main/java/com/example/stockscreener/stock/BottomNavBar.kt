package com.example.stockscreener.stock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stockscreener.Screen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.stockscreener.R

@Composable
fun BottomNavBar(navController: NavController? = null) {
    val items = listOf(Screen.StockList, Screen.Favorites)
    val currentDestination = navController?.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val isSelected = screen.route == currentDestination

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (screen == Screen.StockList) Icons.AutoMirrored.Filled.List else Icons.Default.Star,
                        contentDescription = screen.route
                    )
                },
                label = { Text(
                    text =
                    stringResource(
                        id = if(screen == Screen.Favorites) R.string.watch_list else R.string.stock_list),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ) },
                selected = isSelected,
                onClick = { navController?.navigate(screen.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF6200EA)
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview(){
    BottomNavBar()
}