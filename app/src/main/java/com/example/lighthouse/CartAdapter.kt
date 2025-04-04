package com.example.lighthouse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.cart_item_image)
        val productName: TextView = view.findViewById(R.id.cart_item_name)
        val productPrice: TextView = view.findViewById(R.id.cart_item_price)
        val quantity: TextView = view.findViewById(R.id.quantity_text)
        val increaseBtn: ImageButton = view.findViewById(R.id.increase_quantity)
        val decreaseBtn: ImageButton = view.findViewById(R.id.decrease_quantity)
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)

        holder.productName.text = cartItem.name
        holder.productPrice.text = String.format("$%.2f", cartItem.price)
        holder.quantity.text = cartItem.quantity.toString()

        // Load product image
        Glide.with(holder.itemView.context)
            .load(cartItem.imageResId)
            .into(holder.productImage)

        // Setup quantity controls with bounds
        holder.increaseBtn.setOnClickListener {
            val newQuantity = (cartItem.quantity + 1).coerceAtMost(10)
            if (newQuantity != cartItem.quantity) {
                onQuantityChanged(cartItem, newQuantity)
            }
        }

        holder.decreaseBtn.setOnClickListener {
            val newQuantity = (cartItem.quantity - 1).coerceAtLeast(1)
            if (newQuantity != cartItem.quantity) {
                onQuantityChanged(cartItem, newQuantity)
            }
        }

        // Update button states
        holder.increaseBtn.isEnabled = cartItem.quantity < 10
        holder.decreaseBtn.isEnabled = cartItem.quantity > 1

        holder.deleteBtn.setOnClickListener {
            onDeleteClick(cartItem)
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.productId == newItem.productId && 
               oldItem.size == newItem.size && 
               oldItem.color == newItem.color
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}