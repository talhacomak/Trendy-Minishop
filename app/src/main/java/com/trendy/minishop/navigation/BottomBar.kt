package com.trendy.minishop.navigation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination

@Composable
fun BottomBar(
  currentDestination: NavDestination?,
  onNavigate: (String) -> Unit,
  cartItemCount: Int = 0,
  favoriteCount: Int = 0
) {
  NavigationBar {
    Destinations.tabs.forEach { item ->
      val selected = currentDestination?.route == item.route
      NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(item.route) },
        label = { Text(item.label) },
        icon = {
          if (item.route == "cart" && cartItemCount > 0) {
            BadgedBox(badge = { Badge { Text(cartItemCount.toString()) } }) {
              Icon(item.icon, contentDescription = item.label)
            }
          }
          else if (item.route == "favorites" && favoriteCount > 0) {
            BadgedBox(badge = { Badge { Text(favoriteCount.toString()) } }) {
              Icon(item.icon, contentDescription = item.label)
            }
          }
          else {
            Icon(item.icon, contentDescription = item.label)
          }
        },
      )
    }
  }
}