package com.trendy.minishop.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

object Destinations {
  val Home = Item("home", Icons.Filled.Home, "Home")
  val Favorites = Item("favorites", Icons.Filled.Favorite, "Favorites")
  val Cart = Item("cart", Icons.Filled.ShoppingCart, "Sepet")
  val Profile = Item("profile", Icons.Filled.Person, "Profil")

  val tabs = listOf(Home, Favorites, Cart, Profile)

  data class Item(val route: String, val icon: ImageVector, val label: String)
}