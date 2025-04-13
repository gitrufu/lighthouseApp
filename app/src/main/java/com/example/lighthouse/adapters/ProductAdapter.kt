package com.example.lighthouse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lighthouse.databinding.ItemProductBinding
import com.example.lighthouse.models.Product

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    products: List<Product>? = null
) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    init {
        products?.let { submitList(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProductClick(getItem(position))
                }
            }
        }

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = String.format("₹%.2f", product.price)

            // Load the first image from the product's images
            val imageUrl = when {
                product.imageUrls.isNotEmpty() -> product.imageUrls.first()
                product.imageResIds.isNotEmpty() -> "android.resource://${binding.root.context.packageName}/${product.imageResIds.first()}"
                else -> null
            }

            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.productImage)
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
