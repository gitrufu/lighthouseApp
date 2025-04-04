package com.example.lighthouse.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lighthouse.models.Product
import com.example.lighthouse.R
import kotlinx.coroutines.tasks.await

object FirestoreInitializer {
    private const val TAG = "FirestoreInitializer"
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun initializeCollections(onComplete: (Boolean) -> Unit) {
        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "Error: User must be authenticated to initialize collections")
            onComplete(false)
            return
        }

        // First ensure the current user is an admin
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists() && documentSnapshot.data?.get("isAdmin") == true) {
                    initializeProducts(onComplete)
                } else {
                    // Create or update the user document with admin privileges
                    val userData = hashMapOf(
                        "uid" to currentUser.uid,
                        "email" to currentUser.email,
                        "isAdmin" to true,
                        "createdAt" to System.currentTimeMillis()
                    )
                    
                    db.collection("users").document(currentUser.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            initializeProducts(onComplete)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error setting admin user: ${e.message}")
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking admin status: ${e.message}")
                onComplete(false)
            }
    }

    private fun initializeProducts(onComplete: (Boolean) -> Unit) {
        // Create initial sample products with local images
        val sampleProducts = listOf(
            hashMapOf<String, Any>(
                "id" to "featured_1",
                "name" to "Classic Hoodie",
                "price" to 49.99,
                "description" to "Premium cotton blend hoodie with a modern fit",
                "imageResIds" to listOf<Int>(R.drawable.hoody),
                "imageUrls" to listOf<String>(),
                "sizes" to listOf("S", "M", "L", "XL"),
                "colors" to listOf("Black", "Gray"),
                "featured" to true,
                "suggested" to false,
                "createdAt" to System.currentTimeMillis()
            ),
            hashMapOf<String, Any>(
                "id" to "featured_2",
                "name" to "Classic Cap",
                "price" to 24.99,
                "description" to "Stylish and comfortable cap for everyday wear",
                "imageResIds" to listOf<Int>(R.drawable.cap),
                "imageUrls" to listOf<String>(),
                "sizes" to listOf("One Size"),
                "colors" to listOf("Black", "White"),
                "featured" to true,
                "suggested" to false,
                "createdAt" to System.currentTimeMillis()
            ),
            hashMapOf<String, Any>(
                "id" to "suggested_1",
                "name" to "Bucket Hat",
                "price" to 29.99,
                "description" to "Trendy bucket hat perfect for summer",
                "imageResIds" to listOf<Int>(R.drawable.bucket),
                "imageUrls" to listOf<String>(),
                "sizes" to listOf("S/M", "L/XL"),
                "colors" to listOf("Black", "Beige"),
                "featured" to false,
                "suggested" to true,
                "createdAt" to System.currentTimeMillis()
            ),
            hashMapOf<String, Any>(
                "id" to "suggested_2",
                "name" to "Classic Sweater",
                "price" to 59.99,
                "description" to "Warm and cozy sweater for cold days",
                "imageResIds" to listOf<Int>(R.drawable.sweater),
                "imageUrls" to listOf<String>(),
                "sizes" to listOf("S", "M", "L", "XL"),
                "colors" to listOf("Gray", "Navy"),
                "featured" to false,
                "suggested" to true,
                "createdAt" to System.currentTimeMillis()
            ),
            hashMapOf<String, Any>(
                "id" to "new_1",
                "name" to "Essential T-Shirt",
                "price" to 24.99,
                "description" to "Premium cotton t-shirt with perfect fit",
                "imageResIds" to listOf<Int>(R.drawable.tees),
                "imageUrls" to listOf<String>(),
                "sizes" to listOf("S", "M", "L", "XL", "XXL"),
                "colors" to listOf("White", "Black", "Gray"),
                "featured" to false,
                "suggested" to false,
                "createdAt" to System.currentTimeMillis()
            )
        )

        // Add products to Firestore
        val batch = db.batch()
        sampleProducts.forEach { product ->
            val docRef = db.collection("products").document(product["id"] as String)
            batch.set(docRef, product)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Sample products added successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding sample products: ${e.message}")
                onComplete(false)
            }
    }
}
