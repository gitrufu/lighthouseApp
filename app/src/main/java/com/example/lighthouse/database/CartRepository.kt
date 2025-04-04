package com.example.lighthouse.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.lighthouse.CartItem
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_ADDED_AT
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_COLOR
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_ID
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_IMAGE_RES_ID
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_NAME
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_PRICE
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_PRODUCT_ID
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_QUANTITY
import com.example.lighthouse.database.CartDatabase.Companion.COLUMN_SIZE
import com.example.lighthouse.database.CartDatabase.Companion.TABLE_CART

class CartRepository(context: Context) {
    private val database: CartDatabase = CartDatabase(context)

    fun addToCart(item: CartItem): Boolean {
        return try {
            database.writableDatabase.use { db ->
                // Check if item exists
                val cursor = db.query(
                    TABLE_CART,
                    arrayOf(COLUMN_ID, COLUMN_QUANTITY),
                    "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                    arrayOf(item.productId, item.size, item.color),
                    null, null, null
                )

                if (cursor.moveToFirst()) {
                    // Update existing item
                    val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                    val newQuantity = (currentQuantity + item.quantity).coerceAtMost(10)
                    
                    val values = ContentValues().apply {
                        put(COLUMN_QUANTITY, newQuantity)
                    }
                    
                    db.update(
                        TABLE_CART,
                        values,
                        "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                        arrayOf(item.productId, item.size, item.color)
                    )
                } else {
                    // Insert new item
                    val values = ContentValues().apply {
                        put(COLUMN_PRODUCT_ID, item.productId)
                        put(COLUMN_NAME, item.name)
                        put(COLUMN_PRICE, item.price)
                        put(COLUMN_IMAGE_RES_ID, item.imageResId)
                        put(COLUMN_QUANTITY, item.quantity)
                        put(COLUMN_SIZE, item.size)
                        put(COLUMN_COLOR, item.color)
                        put(COLUMN_ADDED_AT, System.currentTimeMillis())
                    }
                    db.insert(TABLE_CART, null, values)
                }
                cursor.close()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCartItems(): List<CartItem> {
        val items = mutableListOf<CartItem>()
        
        database.readableDatabase.use { db ->
            val cursor = db.query(
                TABLE_CART,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_ADDED_AT DESC"
            )

            while (cursor.moveToNext()) {
                items.add(createCartItemFromCursor(cursor))
            }
            cursor.close()
        }
        
        return items
    }

    fun updateQuantity(productId: String, size: String, color: String, quantity: Int): Boolean {
        return try {
            database.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(COLUMN_QUANTITY, quantity.coerceIn(1, 10))
                }
                
                db.update(
                    TABLE_CART,
                    values,
                    "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                    arrayOf(productId, size, color)
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun removeFromCart(productId: String, size: String, color: String): Boolean {
        return try {
            database.writableDatabase.use { db ->
                db.delete(
                    TABLE_CART,
                    "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                    arrayOf(productId, size, color)
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearCart(): Boolean {
        return try {
            database.writableDatabase.use { db ->
                db.delete(TABLE_CART, null, null)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createCartItemFromCursor(cursor: Cursor): CartItem {
        return CartItem(
            productId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
            imageResId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID)),
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
            size = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIZE)),
            color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR))
        )
    }
}
