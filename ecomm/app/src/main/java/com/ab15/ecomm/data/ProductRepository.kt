package com.ab15.ecomm.data

import com.ab15.ecomm.model.CartItem
import com.ab15.ecomm.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class ProductRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getProductsRealtime(onResult: (List<Product>) -> Unit) {
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects<Product>()
                    onResult(list)
                }
            }
    }

    fun addProduct(product: Product, onComplete: (Boolean) -> Unit) {
        val docRef = if (product.id.isEmpty()) {
            db.collection("products").document()
        } else {
            db.collection("products").document(product.id)
        }
        
        val productWithId = if (product.id.isEmpty()) {
            product.copy(id = docRef.id)
        } else {
            product
        }

        docRef.set(productWithId)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun placeOrder(
        name: String,
        address: String,
        phone: String,
        items: List<CartItem>,
        totalPrice: Double,
        onComplete: (Boolean) -> Unit
    ) {
        val orderData = hashMapOf(
            "customerName" to name,
            "address" to address,
            "phone" to phone,
            "items" to items.map { 
                hashMapOf(
                    "productId" to it.product.id,
                    "name" to it.product.name,
                    "quantity" to it.quantity,
                    "price" to it.product.price
                )
            },
            "totalPrice" to totalPrice,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun seedInitialData(onComplete: () -> Unit) {
        val initialProducts = listOf(
            Product(
                id = "1",
                name = "Premium Wireless Headphones",
                price = "$199.99",
                category = "Electronics",
                description = "High-quality wireless headphones with noise cancellation.",
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop"
            ),
            Product(
                id = "2",
                name = "Smart Fitness Watch",
                price = "$149.50",
                category = "Accessories",
                description = "Track your workouts and health metrics in style.",
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=1000&auto=format&fit=crop"
            )
        )

        val batch = db.batch()
        initialProducts.forEach { product ->
            val docRef = db.collection("products").document(product.id)
            batch.set(docRef, product)
        }

        batch.commit().addOnCompleteListener {
            onComplete()
        }
    }
}
