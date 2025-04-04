package com.example.lighthouse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.lighthouse.adapters.ImagePagerAdapter
import com.example.lighthouse.databinding.ActivityProductDetailBinding
import com.example.lighthouse.models.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import android.widget.TextView
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    companion object {
        private const val EXTRA_PRODUCT = "extra_product"

        fun createIntent(context: Context, product: Product): Intent {
            return Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT, product)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        
        @Suppress("DEPRECATION")
        val product = intent.getParcelableExtra<Product>(EXTRA_PRODUCT)
            ?: throw IllegalStateException("Product must not be null")
        displayProduct(product)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun displayProduct(product: Product) {
        // Set up image viewer
        binding.imageViewPager.adapter = ImagePagerAdapter(product.imageResIds)
        binding.imageViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Set product details
        binding.productName.text = product.name
        binding.productPrice.text = NumberFormat.getCurrencyInstance(Locale.US)
            .format(product.price)
        binding.productDescription.text = product.description

        // Set up size chips
        product.sizes.forEach { size ->
            val chip = createChip(size)
            binding.sizeChipGroup.addView(chip)
        }

        // Set up color chips
        product.colors.forEach { color ->
            val chip = createChip(color)
            binding.colorChipGroup.addView(chip)
        }

        // Set up add to cart button
        binding.addToCartButton.setOnClickListener {
            // TODO: Implement add to cart functionality
        }
    }

    private fun createChip(text: String): Chip {
        return Chip(this).apply {
            this.text = text
            isCheckable = true
            setTextColor(resources.getColorStateList(R.color.chip_text_color, theme))
            chipBackgroundColor = resources.getColorStateList(R.color.chip_background_color, theme)
        }
    }
}
