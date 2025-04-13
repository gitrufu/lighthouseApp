package com.example.lighthouse

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lighthouse.adapters.SubcategoryAdapter
import com.example.lighthouse.databinding.ActivitySubcategoriesBinding
import com.example.lighthouse.models.Product
import com.example.lighthouse.models.SubCategory

class SubcategoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubcategoriesBinding
    private lateinit var adapter: SubcategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubcategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryId = intent.getStringExtra("categoryId") ?: return
        val categoryName = intent.getStringExtra("categoryName") ?: return

        setupToolbar(categoryName)
        setupRecyclerView()
        loadSubcategories(categoryId)
    }

    private fun setupToolbar(categoryName: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = categoryName
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        adapter = SubcategoryAdapter { subcategory ->
            navigateToProducts(subcategory)
        }
        binding.subcategoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SubcategoriesActivity, 2)
            adapter = this@SubcategoriesActivity.adapter
        }
    }

    private fun loadSubcategories(categoryId: String) {
        // Example subcategories - Replace with your actual data source
        val subcategories = when (categoryId) {
            "t_shirt" -> listOf(
                SubCategory("oversized_tee", "Oversized Tees", "Comfortable oversized t-shirts", "@drawable/tees", categoryId),
                SubCategory("classic_tee", "Classic Tees", "Timeless classic t-shirts", "@drawable/classic", categoryId),
                SubCategory("custom_tee", "Custom Design", "Create your own design", "@drawable/custom", categoryId)
            )
            "sweater" -> listOf(
                SubCategory("pullover", "Pullovers", "Warm pullover sweaters", "@drawable/sweater", categoryId),
                SubCategory("cardigan", "Cardigans", "Stylish button-up cardigans", "@drawable/oversz", categoryId),
                SubCategory("hoodie", "Hoodies", "Comfortable hooded sweaters", "@drawable/oversz_hoody", categoryId)
            )
            "hoodie" -> listOf(
                SubCategory("classic_hoody", "Classic Hoody", "Warm winter coats", "@drawable/hoody", categoryId),
                SubCategory("custom_hoody", "Custom Design Hoody", "Classic trench coats", "@drawable/oversz_hoody", categoryId),
            )
            "cap" -> listOf(
                SubCategory("cap", "Cap", "Summer Cap", "@drawable/cap", categoryId),
                SubCategory("bucket_hat", "Bucket Hat", "Summer hats", "@drawable/bucket", categoryId),
            )
            else -> emptyList()
        }
        adapter.submitList(subcategories)
    }

    private fun navigateToProducts(subcategory: SubCategory) {
        // Create a product based on the subcategory
        val product = Product(
            id = when (subcategory.id) {
                "oversized_tee" -> "ot1"
                "classic_tee" -> "ct1"
                "custom_tee" -> "cut1"
                "pullover" -> "p1"
                "cardigan" -> "c1"
                "hoodie" -> "h1"
                else -> subcategory.id
            },
            name = when (subcategory.id) {
                "oversized_tee" -> "Oversized Tee"
                "classic_tee" -> "Classic Tee"
                "custom_tee" -> "Custom Design Tee"
                "pullover" -> "Pullover Sweater"
                "cardigan" -> "Cardigan"
                "hoodie" -> "Hoodie"
                else -> subcategory.name
            },
            price = when (subcategory.id) {
                "oversized_tee" -> 500.00
                "classic_tee" -> 500.00
                "custom_tee" -> 500.00
                "pullover" -> 800.00
                "cardigan" -> 800.00
                "hoodie" -> 1000.00
                "classic_hoody" -> 1000.00
                "custom_hoody" -> 1000.00
                "cap" -> 250.00
                "bucket_hat" -> 300.00
                else -> 0.00
            },
            description = subcategory.description ?: when (subcategory.id) {
                "oversized_tee" -> "Comfortable oversized t-shirt"
                "classic_tee" -> "Classic fit t-shirt"
                "custom_tee" -> "Customizable t-shirt"
                "pullover" -> "Warm pullover sweater"
                "cardigan" -> "Stylish cardigan"
                "hoodie" -> "Comfortable hooded sweater"
                else -> "Product description"
            },
            imageResIds = listOf(when (subcategory.id) {
                "oversized_tee", "classic_tee", "custom_tee" -> R.drawable.oversz
                "pullover", "cardigan" -> R.drawable.sweater
                "hoodie" -> R.drawable.hoody
                "classic_hoody" -> R.drawable.hoody
                "custom_hoody" -> R.drawable.hoody
                "bucket_hat" -> R.drawable.bucket
                "cap" -> R.drawable.cap
                else -> R.drawable.logo
            }),
            sizes = when (subcategory.id) {
                "classic_tee" -> listOf("XS", "S", "M", "L", "XL")
                "cardigan" -> listOf("S", "M", "L")
                else -> listOf("S", "M", "L", "XL")
            },
            colors = when (subcategory.id) {
                "oversized_tee", "custom_tee" -> listOf("Black", "White", "Gray", "Navy")
                "classic_tee" -> listOf("White", "Black", "Navy", "Gray")
                "pullover", "hoodie" -> listOf("Black", "Gray", "Navy")
                "cardigan" -> listOf("Black", "Gray", "Beige")
                else -> listOf("Black", "White", "Gray")
            },
            featured = false,
            suggested = false,
            createdAt = System.currentTimeMillis(),
            imageUrls = listOf()
        )

        // Navigate to subcategory product preview
        val intent = Intent(this, SubCategoryProductPreviewActivity::class.java).apply {
            putExtra("productId", product.id)
            putExtra("productName", product.name)
            putExtra("productPrice", product.price)
            putExtra("productDescription", product.description)
            // Convert image resource IDs to URLs or drawable paths
            val imageUrls = product.imageResIds.map { resId ->
                "android.resource://${packageName}/$resId"
            }
            putStringArrayListExtra("productImages", ArrayList(imageUrls))
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
