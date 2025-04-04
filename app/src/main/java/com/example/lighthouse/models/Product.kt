package com.example.lighthouse.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.example.lighthouse.R

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageResIds: List<Int> = listOf(),
    val imageUrls: List<String> = listOf(),
    val sizes: List<String> = listOf(),
    val colors: List<String> = listOf(),
    val featured: Boolean = false,
    val suggested: Boolean = false,
    val createdAt: Long = 0
) : Parcelable {
    fun getFirstImageResId(): Any? {
        return when {
            imageUrls.isNotEmpty() -> imageUrls.first()
            imageResIds.isNotEmpty() -> imageResIds.first()
            else -> R.drawable.logo
        }
    }
}
