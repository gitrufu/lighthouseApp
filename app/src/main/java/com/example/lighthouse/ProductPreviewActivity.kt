package com.example.lighthouse

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import android.util.Log
import com.example.lighthouse.adapters.ImageSliderAdapter
import com.example.lighthouse.database.DatabaseHelper
import com.example.lighthouse.databinding.ActivityProductPreviewBinding

class ProductPreviewActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ProductPreview"

        fun createIntent(
            context: android.content.Context,
            productId: String,
            productName: String,
            productPrice: Double,
            productDescription: String?,
            productImages: ArrayList<String>
        ): android.content.Intent {
            return android.content.Intent(context, ProductPreviewActivity::class.java).apply {
                putExtra("productId", productId)
                putExtra("productName", productName)
                putExtra("productPrice", productPrice)
                putExtra("productDescription", productDescription)
                putStringArrayListExtra("productImages", productImages)
            }
        }
    }

    private lateinit var binding: ActivityProductPreviewBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private var selectedSize = ""
    private var selectedColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupImageSlider()

        // Get product details from intent
        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productDescription = intent.getStringExtra("productDescription")
        val productImages = intent.getStringArrayListExtra("productImages") ?: arrayListOf()

        Log.d(TAG, "Received product details - ID: $productId, Name: $productName, Price: $productPrice")

        if (productId == null || productName == null) {
            Log.e(TAG, "Missing required product details")
            Toast.makeText(this, "Error: Missing product details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Convert drawable resources to proper URLs
        val processedImages = productImages.map { url ->
            if (url.startsWith("@drawable/")) {
                val resourceName = url.substringAfter("@drawable/")
                val resourceId = resources.getIdentifier(resourceName, "drawable", packageName)
                if (resourceId != 0) {
                    "android.resource://$packageName/$resourceId"
                } else url
            } else url
        }

        setupUI(productName, productPrice, productDescription, processedImages)
        setupSizeSelection()
        setupColorSelection()
        setupAddToCartButton(productId, productName, productPrice, processedImages.firstOrNull())
    }

    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter()
        binding.imageSlider.apply {
            adapter = imageSliderAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        // Connect the TabLayout dots with ViewPager
        TabLayoutMediator(binding.imageSliderDots, binding.imageSlider) { _, _ -> }.attach()

        // Set up page change callback for logging
        binding.imageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "Image slider page selected: $position")
            }
        })
    }

    private fun setupUI(productName: String, productPrice: Double, description: String?, images: List<String>) {
        binding.productName.text = productName
        binding.productPrice.text = String.format("$%.2f", productPrice)
        binding.productDescription.text = description ?: ""

        Log.d(TAG, "Setting up UI with ${images.size} images")
        imageSliderAdapter.setImages(images)
    }

    private fun setupSizeSelection() {
        val sizes = listOf("S", "M", "L", "XL")
        binding.sizeChipGroup.removeAllViews()
        Log.d(TAG, "Setting up size selection with options: $sizes")

        sizes.forEach { size ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = size
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedSize = size
                        Log.d(TAG, "Size selected: $size")
                    }
                }
            }
            binding.sizeChipGroup.addView(chip)
        }
    }

    private fun setupColorSelection() {
        val colors = listOf("Black", "White", "Red", "Blue")
        binding.colorChipGroup.removeAllViews()
        Log.d(TAG, "Setting up color selection with options: $colors")

        colors.forEach { color ->
            val chip = com.google.android.material.chip.Chip(this).apply {
                text = color
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedColor = color
                        Log.d(TAG, "Color selected: $color")
                    }
                }
            }
            binding.colorChipGroup.addView(chip)
        }
    }

    private fun setupAddToCartButton(productId: String, productName: String, productPrice: Double, imageUrl: String?) {
        Log.d(TAG, "Setting up add to cart button for product: $productId")

        binding.addToCartButton.setOnClickListener {
            Log.d(TAG, "Add to cart button clicked")
            Log.d(TAG, "Current selection - Size: $selectedSize, Color: $selectedColor")

            if (selectedSize.isEmpty() || selectedColor.isEmpty()) {
                Log.w(TAG, "Missing selection - Size: ${selectedSize.isEmpty()}, Color: ${selectedColor.isEmpty()}")
                Toast.makeText(this, "Please select both size and color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                Log.d(TAG, "Adding to cart - Product: $productId, Size: $selectedSize, Color: $selectedColor")
                dbHelper.addToCart(
                    productId = productId,
                    name = productName,
                    price = productPrice,
                    size = selectedSize,
                    color = selectedColor,
                    quantity = 1,
                    imageUrl = imageUrl
                )

                Log.d(TAG, "Successfully added to cart")
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to cart", e)
                Toast.makeText(this, "Error adding to cart: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            }
    }
}
