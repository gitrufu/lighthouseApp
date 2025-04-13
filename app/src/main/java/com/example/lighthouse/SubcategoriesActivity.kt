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
                SubCategory("classic_tee", "Classic Tees", "Timeless classic t-shirts", "@drawable/tees", categoryId),
                SubCategory("custom_tee", "Custom Design", "Create your own design", "@drawable/tees", categoryId)
            )
            "sweater" -> listOf(
                SubCategory("pullover", "Pullovers", "Warm pullover sweaters", "@drawable/sweater", categoryId),
                SubCategory("cardigan", "Cardigans", "Stylish button-up cardigans", "@drawable/sweater", categoryId),
                SubCategory("hoodie", "Hoodies", "Comfortable hooded sweaters", "@drawable/sweater", categoryId)
            )
            "coat" -> listOf(
                SubCategory("winter_coat", "Winter Coats", "Warm winter coats", "@drawable/hoody", categoryId),
                SubCategory("trench_coat", "Trench Coats", "Classic trench coats", "@drawable/hoody", categoryId),
                SubCategory("blazer", "Blazers", "Professional blazers", "@drawable/hoody", categoryId)
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
                "oversized_tee" -> 29.99
                "classic_tee" -> 24.99
                "custom_tee" -> 39.99
                "pullover" -> 49.99
                "cardigan" -> 59.99
                "hoodie" -> 44.99
                else -> 29.99
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
                "hoodie" -> R.drawable.classic_hoody
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
