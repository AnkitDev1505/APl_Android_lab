package com.ab15.ecomm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ab15.ecomm.model.Product
import com.ab15.ecomm.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductViewModel = viewModel(),
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onAddProductClick: () -> Unit
) {
    val productList by viewModel.products
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "TrendStore",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = onCartClick) {
                            BadgedBox(
                                badge = {
                                    if (viewModel.cartItems.isNotEmpty()) {
                                        Badge { Text(viewModel.cartItems.sumOf { it.quantity }.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search products...", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        if (productList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val filteredList = productList.filter { it.name.contains(searchQuery, ignoreCase = true) }
                items(filteredList) { product ->
                    ProductCard(
                        product = product, 
                        onProductClick = { onProductClick(product) },
                        onAddToCart = { 
                            viewModel.addToCart(it)
                        }
                    )
                }
            }
        }
    }
}
