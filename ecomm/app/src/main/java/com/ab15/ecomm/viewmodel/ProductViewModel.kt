package com.ab15.ecomm.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ab15.ecomm.data.ProductRepository
import com.ab15.ecomm.model.Product

class ProductViewModel : ViewModel() {

    private val repo = ProductRepository()

    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    fun loadProducts() {
        repo.getProducts {
            _products.value = it
        }
    }
}
