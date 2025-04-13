package com.example.lighthouse.models

data class SubCategory(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val parentCategoryId: String
)
