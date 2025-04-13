package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lighthouse.adapters.CartAdapter
import com.example.lighthouse.database.DatabaseHelper
import com.example.lighthouse.models.CartItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var adapter: CartAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize views
        recyclerView = findViewById(R.id.recycler_cart)
        totalPriceText = findViewById(R.id.total_price)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list
        adapter = CartAdapter(
            onQuantityChanged = { item, newQuantity ->
                updateCartItemQuantity(item, newQuantity)
            },
            onDeleteClick = { item ->
                deleteCartItem(item)
            }
        )
        recyclerView.adapter = adapter

        // Load cart items
        loadCartItems()

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup checkout button
        findViewById<Button>(R.id.checkout_button).setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val orderId = dbHelper.createOrder()
                Toast.makeText(this, "Order placed successfully! Order ID: $orderId", Toast.LENGTH_LONG).show()
                finish() // Go back to previous screen
            } catch (e: Exception) {
                Toast.makeText(this, "Error placing order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCartItems() {
        cartItems.clear()
        val dbItems = dbHelper.getCartItems()
        cartItems.addAll(dbItems.map { dbItem ->
            com.example.lighthouse.models.CartItem(
                productId = dbItem.productId,
                name = dbItem.name,
                price = dbItem.price,
                size = dbItem.size,
                color = dbItem.color,
                quantity = dbItem.quantity,
                imageUrl = dbItem.imageUrl
            )
        })
        adapter.submitList(cartItems.toList())
        updateTotalPrice()
    }

    private fun updateCartItemQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteCartItem(item)
            return
        }
        
        try {
            dbHelper.updateCartItemQuantity(
                productId = item.productId,
                size = item.size,
                color = item.color,
                newQuantity = newQuantity
            )
            loadCartItems()
            Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating quantity: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCartItem(item: CartItem) {
        if (dbHelper.removeFromCart(item.productId, item.size, item.color)) {
            loadCartItems()
            Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error removing item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.price * it.quantity }
        totalPriceText.text = String.format("Total: $%.2f", total)
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_cart // Highlight cart tab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cart -> true
                R.id.nav_home -> {
                    startActivity(Intent(this@CartActivity, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this@CartActivity, CategoriesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this@CartActivity, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}