package com.ab15.ecomm.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ab15.ecomm.data.ProductRepository
import com.ab15.ecomm.model.CartItem
import com.ab15.ecomm.model.Product

class ProductViewModel : ViewModel() {

    private val repo = ProductRepository()

    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    // Cart state
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    fun loadProducts() {
        repo.getProductsRealtime {
            if (it.isEmpty()) {
                // If really empty and not just loading, seed once
                repo.seedInitialData {
                    // Realtime listener will pick it up
                }
            } else {
                _products.value = it
            }
        }
    }

    fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        repo.addProduct(product, onComplete)
    }

    fun placeOrder(
        name: String,
        address: String,
        phone: String,
        onComplete: (Boolean) -> Unit
    ) {
        repo.placeOrder(
            name = name,
            address = address,
            phone = phone,
            items = _cartItems.toList(),
            totalPrice = getTotalPrice(),
            onComplete = onComplete
        )
    }

    fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
            // Force recomposition for list
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy()
        } else {
            _cartItems.add(CartItem(product))
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItems.remove(cartItem)
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            val index = _cartItems.indexOf(cartItem)
            if (index != -1) {
                _cartItems[index] = cartItem.copy(quantity = newQuantity)
            }
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { 
            val priceValue = it.product.price.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
            priceValue * it.quantity
        }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}
