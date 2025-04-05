package com.example.lighthouse

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.lighthouse.adapters.ProductImageAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View
import androidx.core.content.ContextCompat
import com.example.lighthouse.database.CartRepository

class ProductPreviewActivity : AppCompatActivity() {
    private companion object {
        private const val TAG = "ProductPreviewActivity"
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var productNameText: TextView
    private lateinit var productPriceText: TextView
    private lateinit var productDescriptionText: TextView
    private lateinit var sizeChipGroup: ChipGroup
    private lateinit var colorChipGroup: ChipGroup

    private lateinit var addToCartButton: MaterialButton
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_preview)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        viewPager = findViewById(R.id.product_image_pager)
        productNameText = findViewById(R.id.product_name)
        productPriceText = findViewById(R.id.product_price)
        productDescriptionText = findViewById(R.id.product_description)
        sizeChipGroup = findViewById(R.id.size_chip_group)
        colorChipGroup = findViewById(R.id.color_chip_group)
        addToCartButton = findViewById(R.id.add_to_cart_button)
        // Initialize add to cart button
        addToCartButton.text = "Add to Cart"
        addToCartButton.isEnabled = true

        // Get product details from intent
        val productId = intent.getStringExtra("product_id")
        if (productId == null) {
            Log.e(TAG, "No product ID provided")
            Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load full product details
        loadProductDetails(productId)

        // Set up add to cart button with monochromatic theme
        setupAddToCartButton(productId)

    }

    private fun loadProductDetails(productId: String) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Log.e(TAG, "Product not found: $productId")
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                // Update UI with product details
                val name = document.getString("name") ?: ""
                val price = document.getDouble("price") ?: 0.0
                val description = document.getString("description") ?: ""
                val imageResIds = (document.get("imageResIds") as? List<*>)?.mapNotNull {
                    when (it) {
                        is Long -> it.toInt()
                        is Int -> it
                        else -> null
                    }
                } ?: listOf()
                val sizes = (document.get("sizes") as? List<*>)?.mapNotNull { it as? String } ?: listOf()
                val colors = (document.get("colors") as? List<*>)?.mapNotNull { it as? String } ?: listOf()

                productNameText.text = name
                productPriceText.text = getString(R.string.price_format, price)
                productDescriptionText.text = description

                // Set up image slider
                viewPager.adapter = ProductImageAdapter(imageResIds)

                // Set up size and color selection
                setupSizeSelection(sizes)
                setupColorSelection(colors)

                // Set up add to cart button after loading product details
                setupAddToCartButton(productId)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error loading product: ${e.message}")
                Toast.makeText(this, R.string.error_loading_product, Toast.LENGTH_SHORT).show()
                finish()
            }
    }



    private fun setupSizeSelection(sizes: List<String>) {
        sizeChipGroup.removeAllViews()
        sizes.forEach { size ->
            addChipToGroup(sizeChipGroup, size)
        }
    }

    private fun setupColorSelection(colors: List<String>) {
        colorChipGroup.removeAllViews()
        colors.forEach { color ->
            addChipToGroup(colorChipGroup, color)
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, text: String) {
        val chip = Chip(this).apply {
            this.text = text
            isCheckable = true
            chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            chipStrokeWidth = resources.displayMetrics.density * 1 // Convert 1dp to pixels
            chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            setTextColor(ContextCompat.getColor(context, R.color.black))
            checkedIcon = null
        }
        chipGroup.addView(chip)
    }

    private fun setupAddToCartButton(productId: String) {
        addToCartButton.apply {
            setText(R.string.add_to_cart)
            isEnabled = true
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setOnClickListener {
                addToCart(productId)
            }
        }
    }

    private fun addToCart(productId: String) {
        Log.d(TAG, "Starting addToCart for product: $productId")
        // Disable add to cart button to prevent multiple clicks
        addToCartButton.isEnabled = false

        val selectedSize = findViewById<Chip>(sizeChipGroup.checkedChipId)?.text?.toString()
        val selectedColor = findViewById<Chip>(colorChipGroup.checkedChipId)?.text?.toString()
        
        Log.d(TAG, "Selected options - Size: $selectedSize, Color: $selectedColor")

        if (selectedSize == null || selectedColor == null) {
            Toast.makeText(this, R.string.select_size_color, Toast.LENGTH_SHORT).show()
            addToCartButton.isEnabled = true
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, R.string.sign_in_to_add, Toast.LENGTH_SHORT).show()
            addToCartButton.isEnabled = true
            return
        }

        // Show loading state
        addToCartButton.setText(R.string.adding_to_cart)

        Log.d(TAG, "Fetching product details from Firestore for ID: $productId")
        // Get product details from Firestore
        db.collection("products").document(productId).get().addOnSuccessListener { productDoc ->
            if (!productDoc.exists()) {
                Log.e(TAG, "Product document does not exist in Firestore")
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
                addToCartButton.text = "Add to Cart"
                addToCartButton.isEnabled = true
                return@addOnSuccessListener
            }
            Log.d(TAG, "Successfully retrieved product document from Firestore")

            val name = productDoc.getString("name") ?: ""
            val price = productDoc.getDouble("price") ?: 0.0
            val imageResId = (productDoc.get("imageResIds") as? List<*>)?.firstOrNull() as? Long ?: 0L
            
            Log.d(TAG, "Retrieved product details - Name: $name, Price: $price, ImageResId: $imageResId")

            // Create cart item with default quantity of 1
            val cartItem = CartItem(
                productId = productId,
                name = name,
                price = price,
                imageResId = imageResId,
                quantity = 1, // Default quantity, can be modified in cart
                size = selectedSize,
                color = selectedColor
            )

            // Add to local SQLite database
            val cartRepository = CartRepository(this)
            val success = cartRepository.addToCart(cartItem)

            if (success) {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show()
                addToCartButton.text = "Add to Cart"
                addToCartButton.isEnabled = true
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error getting product details: ${e.message}")
            Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show()
            addToCartButton.text = "Add to Cart"
            addToCartButton.isEnabled = true
        }
    }
}
