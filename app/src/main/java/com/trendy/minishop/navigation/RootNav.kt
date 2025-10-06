package com.trendy.minishop.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trendy.feature.home.HomeScreen
import com.trendy.feature.favorites.FavoritesScreen
import com.trendy.feature.cart.CartScreen
import androidx.compose.material3.Scaffold

@Composable
fun RootNav() {
  val navController = rememberNavController()

  Scaffold(
    bottomBar = {
      BottomBar(
        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
        onNavigate = { route ->
          navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }
  ) { padding ->
    NavHost(
      navController = navController,
      startDestination = Destinations.Home.route,
      modifier = androidx.compose.ui.Modifier.padding(padding)
    ) {
      composable(Destinations.Home.route) {
        HomeScreen(onOpenCart = { navController.navigate(Destinations.Cart.route) })
      }
      composable(Destinations.Favorites.route) {
        FavoritesScreen(
          onRemoveFavorite = { /* opsiyonel: üstten kontrol etmek istersen buraya VM çağrısı taşıyabilirsin */ }
        )
      }
      composable(Destinations.Cart.route) { CartScreen() }
      composable(Destinations.Profile.route) { ProfilePlaceholder() }
    }
  }
}

private object Destinations {
  val Home = Item("home", Icons.Filled.Home, "Home")
  val Favorites = Item("favorites", Icons.Filled.Favorite, "Favorites")
  val Cart = Item("cart", Icons.Filled.ShoppingCart, "Sepet")
  val Profile = Item("profile", Icons.Filled.Person, "Profil")

  val tabs = listOf(Home, Favorites, Cart, Profile)

  data class Item(val route: String, val icon: ImageVector, val label: String)
}

@Composable
private fun BottomBar(
  currentDestination: NavDestination?,
  onNavigate: (String) -> Unit
) {
  NavigationBar {
    Destinations.tabs.forEach { item ->
      val selected = currentDestination?.route == item.route
      NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(item.route) },
        icon = { Icon(item.icon, contentDescription = item.label) },
        label = { Text(item.label) }
      )
    }
  }
}

@Composable
private fun ProfilePlaceholder() {
  androidx.compose.foundation.layout.Box(
    modifier = androidx.compose.ui.Modifier
      .fillMaxSize(),
    contentAlignment = androidx.compose.ui.Alignment.Center
  ) {
    Text("Profil (yakında)")
  }
}
