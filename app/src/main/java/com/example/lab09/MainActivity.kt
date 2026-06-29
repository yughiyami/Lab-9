package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab09.ui.screens.ProductDetailScreen
import com.example.lab09.ui.screens.ProductsScreen
import com.example.lab09.ui.theme.Lab09Theme
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
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "products") {
                        composable("products") {
                            ProductsScreen(
                                onProductClick = { id -> navController.navigate("detail/$id") }
                            )
                        }
                        composable("detail/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id")?.toIntOrNull() ?: 0
                            ProductDetailScreen(
                                productId = id,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
