package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab09.ui.screens.CartScreen
import com.example.lab09.ui.screens.CategoriesScreen
import com.example.lab09.ui.screens.CategoryProductsScreen
import com.example.lab09.ui.screens.FavoritesScreen
import com.example.lab09.ui.screens.ProductDetailScreen
import com.example.lab09.ui.screens.ProductFormScreen
import com.example.lab09.ui.screens.ProductsScreen
import com.example.lab09.ui.theme.Lab09Theme
import com.example.lab09.ui.viewmodel.CartViewModel
import com.example.lab09.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { Lab09App() }
            }
        }
    }
}

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Tab("home", "Inicio", Icons.Filled.Home)
    data object Categories : Tab("categories", "Categorías", Icons.Filled.Category)
    data object Favorites : Tab("favorites", "Favoritos", Icons.Filled.Favorite)
    data object Cart : Tab("cart", "Carrito", Icons.Filled.ShoppingCart)
}

private val tabs = listOf(Tab.Home, Tab.Categories, Tab.Favorites, Tab.Cart)

@Composable
fun Lab09App() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    // shared stores for badge counts
    val favoritesVm: FavoritesViewModel = hiltViewModel()
    val cartVm: CartViewModel = hiltViewModel()
    val favorites by favoritesVm.favoritesStore.favorites.collectAsStateWithLifecycle()
    val cart by cartVm.cartStore.items.collectAsStateWithLifecycle()
    val cartCount = cart.values.sumOf { it.quantity }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = backStack?.destination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                when (tab) {
                                    Tab.Favorites -> BadgedBox(badge = {
                                        if (favorites.isNotEmpty()) Badge { Text("${favorites.size}") }
                                    }) { Icon(tab.icon, contentDescription = tab.label) }
                                    Tab.Cart -> BadgedBox(badge = {
                                        if (cartCount > 0) Badge { Text("$cartCount") }
                                    }) { Icon(tab.icon, contentDescription = tab.label) }
                                    else -> Icon(tab.icon, contentDescription = tab.label)
                                }
                            },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Home.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(Tab.Home.route) {
                ProductsScreen(
                    onProductClick = { id -> navController.navigate("detail/$id") },
                    onAddProduct = { navController.navigate("form?id=") }
                )
            }
            composable(Tab.Categories.route) {
                CategoriesScreen(
                    onCategoryClick = { cat -> navController.navigate("category/$cat") }
                )
            }
            composable(Tab.Favorites.route) {
                FavoritesScreen(
                    onProductClick = { id -> navController.navigate("detail/$id") }
                )
            }
            composable(Tab.Cart.route) { CartScreen() }

            composable("detail/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
                ProductDetailScreen(
                    productId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { pid -> navController.navigate("form?id=$pid") }
                )
            }
            composable("category/{name}") { entry ->
                val name = entry.arguments?.getString("name").orEmpty()
                CategoryProductsScreen(
                    category = name,
                    onProductClick = { id -> navController.navigate("detail/$id") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "form?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { entry ->
                val id = entry.arguments?.getString("id")?.toIntOrNull()
                ProductFormScreen(
                    productId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
