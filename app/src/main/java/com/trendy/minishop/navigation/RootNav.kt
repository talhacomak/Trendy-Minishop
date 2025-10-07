package com.trendy.minishop.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trendy.feature.home.HomeScreen
import com.trendy.feature.favorites.FavoritesScreen
import com.trendy.feature.cart.CartScreen
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trendy.minishop.view_model.MainViewModel

@Composable
fun RootNav(
  mainVM: MainViewModel = hiltViewModel()
) {
  val navController = rememberNavController()
  val cartCount by mainVM.cartItemCountState.collectAsState()
  val favoriteCount by mainVM.favoriteCountState.collectAsState()

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
        },
        cartCount,
        favoriteCount
      )
    }
  ) { padding ->
    NavHost(
      navController = navController,
      startDestination = Destinations.Home.route,
      modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
    ) {
      composable(Destinations.Home.route) {
        HomeScreen(onOpenCart = { navController.navigate(Destinations.Cart.route) })
      }
      composable(Destinations.Favorites.route) {
        FavoritesScreen(
          onRemoveFavorite = { /* TODO */ }
        )
      }
      composable(Destinations.Cart.route) { CartScreen() }
      composable(Destinations.Profile.route) { ProfilePlaceholder() }
    }
  }
}

@Composable
private fun ProfilePlaceholder() {
  androidx.compose.foundation.layout.Box(
    modifier = Modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text("Profil (yakında)")
  }
}
