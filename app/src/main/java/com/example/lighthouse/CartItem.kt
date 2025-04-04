package com.example.lighthouse

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val imageResId: Long,
    var quantity: Int,
    val size: String,
    val color: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItem
        return productId == other.productId && size == other.size && color == other.color
    }

    override fun hashCode(): Int {
        var result = productId.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + color.hashCode()
        return result
    }
}