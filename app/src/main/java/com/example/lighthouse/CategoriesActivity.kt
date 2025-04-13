package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lighthouse.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoriesActivity : AppCompatActivity() {

    private fun getCategoryId(categoryName: String): String {
        return when (categoryName) {
            "T-Shirts" -> "t_shirt"
            "Hoodies" -> "hoodie"
            "Sweaters" -> "sweater"
            "Caps" -> "cap"
            else -> categoryName.lowercase().replace(" ", "_")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_categories)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Sample category data
        val categories = listOf(
            Category("T-Shirts", R.drawable.tees),
            Category("Hoodies", R.drawable.hoody),
            Category("Sweaters", R.drawable.sweater),
            Category("Caps", R.drawable.bucket)
        )

        // Set adapter
        val adapter = CategoryAdapter(categories) { category ->
            // Navigate to subcategories
            val intent = Intent(this, SubcategoriesActivity::class.java)
            intent.putExtra("categoryId", getCategoryId(category.name))
            intent.putExtra("categoryName", category.name)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_categories // Highlight categories tab
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_categories -> true
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
} 