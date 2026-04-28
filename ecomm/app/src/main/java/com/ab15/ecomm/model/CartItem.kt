package com.ab15.ecomm.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)
