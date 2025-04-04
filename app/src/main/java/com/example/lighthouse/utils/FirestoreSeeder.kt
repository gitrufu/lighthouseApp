package com.example.lighthouse.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lighthouse.models.Product
import com.example.lighthouse.R

object FirestoreSeeder {
    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "FirestoreSeeder"

    fun seedSampleProducts(onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Starting to seed sample products...")
        
        // First, check if we can access Firestore
        db.collection("products").limit(1).get()
            .addOnSuccessListener { 
                Log.d(TAG, "Successfully connected to Firestore. Creating sample products...")
                createSampleProducts(onComplete)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to connect to Firestore: ${e.message}", e)
                onComplete(false)
            }
    }

    private fun createSampleProducts(onComplete: (Boolean) -> Unit) {
        val batch = db.batch()
        
        // Sample products with local resource images
        val sampleProducts = listOf(
            Product(
                id = "featured_1",
                name = "Classic Hoodie",
                price = 49.99,
                description = "Premium cotton blend hoodie with a modern fit",
                imageResIds = listOf(R.drawable.product_hoody),
                sizes = listOf("S", "M", "L", "XL"),
                colors = listOf("Black", "Gray"),
                featured = true,
                suggested = false,
                createdAt = System.currentTimeMillis()
            ),
            Product(
                id = "featured_2",
                name = "Classic Cap",
                price = 24.99,
                description = "Stylish and comfortable cap for everyday wear",
                imageResIds = listOf(R.drawable.product_cap),
                sizes = listOf("One Size"),
                colors = listOf("Black", "White"),
                featured = true,
                suggested = false,
                createdAt = System.currentTimeMillis()
            ),
            Product(
                id = "suggested_1",
                name = "Bucket Hat",
                price = 29.99,
                description = "Trendy bucket hat perfect for summer",
                imageResIds = listOf(R.drawable.product_bucket),
                sizes = listOf("S/M", "L/XL"),
                colors = listOf("Black", "Beige"),
                featured = false,
                suggested = true,
                createdAt = System.currentTimeMillis()
            ),
            Product(
                id = "suggested_2",
                name = "Classic Sweater",
                price = 59.99,
                description = "Warm and cozy sweater for cold days",
                imageResIds = listOf(R.drawable.product_sweater),
                sizes = listOf("S", "M", "L", "XL"),
                colors = listOf("Gray", "Navy"),
                featured = false,
                suggested = true,
                createdAt = System.currentTimeMillis()
            ),
            Product(
                id = "new_1",
                name = "Essential T-Shirt",
                price = 24.99,
                description = "Premium cotton t-shirt with perfect fit",
                imageResIds = listOf(R.drawable.product_tees),
                sizes = listOf("S", "M", "L", "XL", "XXL"),
                colors = listOf("White", "Black", "Gray"),
                featured = false,
                suggested = false,
                createdAt = System.currentTimeMillis()
            )
        )

        // Add each product to the batch
        sampleProducts.forEach { product ->
            val docRef = db.collection("products").document(product.id)
            batch.set(docRef, product)
        }

        // Commit the batch
        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Successfully seeded sample products")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to seed sample products: ${e.message}", e)
                onComplete(false)
            }
    }
}
