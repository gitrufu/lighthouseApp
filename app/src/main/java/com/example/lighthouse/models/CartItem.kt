package com.example.lighthouse.models

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val size: String,
    val color: String,
    val quantity: Int,
    val imageUrl: String?
)
