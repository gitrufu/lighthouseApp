package com.example.lighthouse.extensions

import com.example.lighthouse.models.Product
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toProduct(): Product? {
    return try {
        val imageResIds = (get("imageResIds") as? List<*>)?.mapNotNull { 
            when (it) {
                is Long -> it.toInt()
                is Int -> it
                else -> null
            }
        } ?: listOf()

        Product(
            id = id,
            name = getString("name") ?: "",
            price = getDouble("price") ?: 0.0,
            description = getString("description") ?: "",
            imageResIds = imageResIds,
            sizes = get("sizes") as? List<String> ?: listOf(),
            colors = get("colors") as? List<String> ?: listOf(),
            featured = getBoolean("featured") ?: false,
            suggested = getBoolean("suggested") ?: false,
            createdAt = getLong("createdAt") ?: 0
        )
    } catch (e: Exception) {
        null
    }
}
