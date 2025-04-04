package com.example.lighthouse

data class Product(
    val name: String,
    val price: Double,
    val imageResource: Int? = null,
    val imageUrl: String? = null
) {
    fun getImageSource(): Any? {
        return imageResource ?: imageUrl
    }
} 