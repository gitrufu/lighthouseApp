package com.example.lighthouse.models

data class Order(
    val id: String,
    val date: Long,
    val status: String,
    val total: Double
)
