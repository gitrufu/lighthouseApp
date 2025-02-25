package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lighthouse.adapters.ProductsAdapter
import com.example.lighthouse.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_products)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns for grid

        // Sample product list
        val productList = listOf(
            Product("Sneakers", 59.99, "https://example.com/sneakers.jpg"),
            Product("Backpack", 39.99, "https://example.com/backpack.jpg"),
            Product("Watch", 129.99, "https://example.com/watch.jpg"),
            Product("Headphones", 79.99, "https://example.com/headphones.jpg")
        )

        // Set Adapter
        val adapter = ProductsAdapter(productList) { product ->
            Toast.makeText(this, "Clicked: ${product.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_categories -> { /* Open Categories */ true
                }

                R.id.nav_cart -> { /* Open Cart */ true
                }

                R.id.nav_settings -> {
                    true
                }

                else -> false
            }
        }
    }
}
