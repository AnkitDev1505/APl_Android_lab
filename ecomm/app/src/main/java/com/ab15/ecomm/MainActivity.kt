package com.ab15.ecomm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ab15.ecomm.ui.*
import com.ab15.ecomm.ui.theme.EcommTheme
import com.ab15.ecomm.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcommTheme {
                val navController = rememberNavController()
                val viewModel: ProductViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onProductClick = { product ->
                                navController.navigate("detail/${product.id}")
                            },
                            onCartClick = {
                                navController.navigate("cart")
                            },
                            onAddProductClick = {
                                navController.navigate("add_product")
                            }
                        )
                    }
                    composable("add_product") {
                        AddProductScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("detail/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")
                        ProductDetailScreen(
                            productId = productId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("cart") {
                        CartScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onCheckout = { navController.navigate("checkout") }
                        )
                    }
                    composable("checkout") {
                        CheckoutScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onOrderPlaced = {
                                viewModel.clearCart()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
