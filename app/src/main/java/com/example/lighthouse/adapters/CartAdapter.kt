package com.example.lighthouse.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lighthouse.R
import com.example.lighthouse.databinding.ItemCartBinding
import com.example.lighthouse.models.CartItem

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.apply {
                cartItemName.text = item.name
                cartItemPrice.text = String.format("₹%.2f", item.price)

                // Load image using Glide
                // Load image using Glide
                Glide.with(itemView.context)
                    .load(item.imageUrl ?: R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(cartItemImage)

                // Quantity controls
                quantityText.text = item.quantity.toString()
                
                increaseQuantity.setOnClickListener {
                    val newQuantity = item.quantity + 1
                    onQuantityChanged(item, newQuantity)
                }

                decreaseQuantity.setOnClickListener {
                    if (item.quantity > 1) {
                        val newQuantity = item.quantity - 1
                        onQuantityChanged(item, newQuantity)
                    }
                }

                // Delete button
                deleteItem.setOnClickListener {
                    onDeleteClick(item)
                }
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId && 
                   oldItem.size == newItem.size && 
                   oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
