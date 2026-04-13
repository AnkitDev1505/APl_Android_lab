package com.ab15.ecomm.data

import com.ab15.ecomm.model.Product

class ProductRepository {

    // Commented out Firebase for now as per user request
    // private val db = FirebaseFirestore.getInstance()

    fun getProducts(onResult: (List<Product>) -> Unit) {
        // Returning dummy data for now
        val dummyProducts = listOf(
            Product("1", "Smartphone", "$699"),
            Product("2", "Laptop", "$1299"),
            Product("3", "Headphones", "$199"),
            Product("4", "Smartwatch", "$249"),
            Product("5", "Tablet", "$499")
        )
        onResult(dummyProducts)
        
        /* 
        // Real implementation
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.toObject(Product::class.java) }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
        */
    }
}
