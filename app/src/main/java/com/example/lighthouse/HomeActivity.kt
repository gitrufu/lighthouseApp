package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.lighthouse.adapters.ProductAdapter
import com.example.lighthouse.extensions.toProduct
import com.example.lighthouse.models.Product
import com.example.lighthouse.utils.FirestoreInitializer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.graphics.Rect

class HomeActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private companion object {
        private const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()
        Log.d(TAG, "Initializing Firestore instance")

        // Check if Firestore is properly initialized
        checkFirestoreAndLoadProducts()
    }

    private fun checkFirestoreAndLoadProducts() {
        Log.d(TAG, "Checking Firestore initialization...")
        db.collection("products").limit(1).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d(TAG, "No products found, initializing Firestore...")
                    // Show initialization button with monochromatic design
                    findViewById<Button>(R.id.btn_init_data)?.apply {
                        visibility = View.VISIBLE
                        setBackgroundColor(getColor(R.color.black))
                        setTextColor(getColor(R.color.white))
                        text = "Initialize Store Data"
                        setOnClickListener {
                            FirestoreInitializer.initializeCollections { success ->
                                runOnUiThread {
                                    if (success) {
                                        Toast.makeText(this@HomeActivity, "Store initialized!", Toast.LENGTH_SHORT).show()
                                        visibility = View.GONE
                                        // Load products after initialization
                                        setupProductDisplays()
                                    } else {
                                        Toast.makeText(this@HomeActivity, "Failed to initialize store", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Products exist, setting up displays")
                    setupProductDisplays()
                }
            }
            .addOnFailureListener { e ->
                val errorMsg = "Error checking Firestore: ${e.message}"
                Log.e(TAG, errorMsg, e)
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupProductDisplays() {
        setupFeaturedProducts()
        setupSuggestedProducts()
        setupNewArrivals()
        setupBottomNavigation()
    }

    private fun setupFeaturedProducts() {
        Log.d(TAG, "Setting up featured products...")
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_featured_products)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            // Remove any existing item decorations to avoid duplicate spacing
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            // Add item decoration for spacing
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val column = position % 2

                    outRect.left = if (column == 0) spacing else spacing / 2
                    outRect.right = if (column == 0) spacing / 2 else spacing
                    outRect.top = spacing
                    outRect.bottom = spacing
                }
            })
            clipToPadding = false
            setPadding(spacing, spacing, spacing, spacing)
        }

        db.collection("products")
            .whereEqualTo("featured", true)
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { doc ->
                    doc.toProduct()
                }
                Log.d(TAG, "Featured products found: ${products.size}")
                val adapter = ProductAdapter(
                    onProductClick = { product -> openProductPreview(product) },
                    products = products
                )
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching featured products: ${e.message}", e)
            }
    }

    private fun setupSuggestedProducts() {
        Log.d(TAG, "Setting up suggested products...")
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_suggested_products)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        db.collection("products")
            .whereEqualTo("suggested", true)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { doc ->
                    doc.toProduct()
                }
                Log.d(TAG, "Suggested products found: ${products.size}")
                val adapter = ProductAdapter(
                    onProductClick = { product -> openProductPreview(product) },
                    products = products
                )
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching suggested products: ${e.message}", e)
            }
    }

    private fun setupNewArrivals() {
        Log.d(TAG, "Setting up new arrivals...")
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_new_arrivals)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        db.collection("products")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { doc ->
                    doc.toProduct()
                }
                Log.d(TAG, "New arrival products found: ${products.size}")
                val adapter = ProductAdapter(
                    onProductClick = { product -> openProductPreview(product) },
                    products = products
                )
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching new arrivals: ${e.message}", e)
            }
    }

    private fun openProductPreview(product: Product) {
        val images = ArrayList(product.imageUrls)
        startActivity(ProductPreviewActivity.createIntent(
            context = this,
            productId = product.id,
            productName = product.name,
            productPrice = product.price,
            productDescription = product.description,
            productImages = images
        ))
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_categories -> {
                    startActivity(Intent(this, CategoriesActivity::class.java))
                    false
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    false
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}