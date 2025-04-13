package com.example.lighthouse.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.lighthouse.models.Order

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, Companion.DATABASE_NAME, null, Companion.DATABASE_VERSION) {
    companion object {
        private const val TAG = "DatabaseHelper"
        const val DATABASE_NAME = "LighthouseDB"
        const val DATABASE_VERSION = 3

        // Orders table
        const val TABLE_ORDERS = "orders"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_ORDER_STATUS = "status"
        const val COLUMN_ORDER_TOTAL = "total"

        // Table name
        const val TABLE_CART = "cart"

        // Column names
        const val COLUMN_ID = "id"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_PRODUCT_NAME = "product_name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_SIZE = "size"
        const val COLUMN_COLOR = "color"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create cart table
        val createCartTable = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_ID TEXT NOT NULL,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_SIZE TEXT NOT NULL,
                $COLUMN_COLOR TEXT NOT NULL,
                $COLUMN_QUANTITY INTEGER NOT NULL,
                $COLUMN_IMAGE_URL TEXT
            )
        """.trimIndent()
        db.execSQL(createCartTable)

        // Create orders table
        val createOrdersTable = """
            CREATE TABLE $TABLE_ORDERS (
                $COLUMN_ORDER_ID TEXT PRIMARY KEY,
                $COLUMN_ORDER_DATE INTEGER NOT NULL,
                $COLUMN_ORDER_STATUS TEXT NOT NULL,
                $COLUMN_ORDER_TOTAL REAL NOT NULL
            )
        """.trimIndent()
        db.execSQL(createOrdersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from version $oldVersion to $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        onCreate(db)
    }

    private fun checkDatabase() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            Log.w(TAG, "Database file does not exist: ${dbFile.absolutePath}")
            // This will trigger onCreate
            val db = this.writableDatabase
            db.close()
        } else {
            Log.d(TAG, "Database exists at: ${dbFile.absolutePath}")
            // Verify table exists
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                arrayOf(TABLE_CART)
            )
            val tableExists = cursor.moveToFirst()
            cursor.close()
            if (!tableExists) {
                Log.w(TAG, "Cart table does not exist, recreating...")
                onCreate(db)
            }
            db.close()
        }
    }

    fun addToCart(productId: String, name: String, price: Double, size: String, color: String, quantity: Int, imageUrl: String?) {
        checkDatabase()
        Log.d(TAG, "Adding to cart - Product: $productId, Size: $size, Color: $color, Quantity: $quantity")
        
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_ID, productId)
            put(COLUMN_PRODUCT_NAME, name)
            put(COLUMN_PRICE, price)
            put(COLUMN_SIZE, size)
            put(COLUMN_COLOR, color)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_IMAGE_URL, imageUrl)
        }

        try {
            // Check if item already exists
            Log.d(TAG, "Checking if item exists in cart")
            val cursor = db.query(
                TABLE_CART,
                null,
                "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                arrayOf(productId, size, color),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                // Update existing item
                val existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                val newQuantity = existingQuantity + quantity
                Log.d(TAG, "Updating existing item. Old quantity: $existingQuantity, New quantity: $newQuantity")
                
                values.put(COLUMN_QUANTITY, newQuantity)
                val updateResult = db.update(
                    TABLE_CART,
                    values,
                    "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?",
                    arrayOf(productId, size, color)
                )
                Log.d(TAG, "Update result: $updateResult rows affected")
            } else {
                // Insert new item
                Log.d(TAG, "Inserting new item into cart")
                val insertResult = db.insert(TABLE_CART, null, values)
                Log.d(TAG, "Insert result: Row ID = $insertResult")
            }

            cursor.close()
            Log.d(TAG, "Successfully added/updated item in cart")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding item to cart", e)
            throw e
        }

        db.close()
    }

    fun getCartItems(): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        var cursor: android.database.Cursor? = null
        var db: SQLiteDatabase? = null

        try {
            db = this.readableDatabase
            cursor = db.query(
                TABLE_CART,
                null,
                null,
                null,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    val item = CartItem(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        productId = getString(getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        name = getString(getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                        price = getDouble(getColumnIndexOrThrow(COLUMN_PRICE)),
                        size = getString(getColumnIndexOrThrow(COLUMN_SIZE)),
                        color = getString(getColumnIndexOrThrow(COLUMN_COLOR)),
                        quantity = getInt(getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        imageUrl = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                    )
                    cartItems.add(item)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cart items: ${e.message}")
            throw e
        } finally {
            cursor?.close()
            db?.close()
        }
        return cartItems
    }

    fun removeFromCart(productId: String, size: String, color: String): Boolean {
        val db = this.writableDatabase
        val selection = "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?"
        val selectionArgs = arrayOf(productId, size, color)
        
        val deletedRows = db.delete(TABLE_CART, selection, selectionArgs)
        db.close()
        
        return deletedRows > 0
    }

    fun clearCart(db: SQLiteDatabase? = null) {
        val shouldClose = db == null
        val database = db ?: this.writableDatabase
        database.delete(TABLE_CART, null, null)
        if (shouldClose) {
            database.close()
        }
    }

    fun updateCartItemQuantity(productId: String, size: String, color: String, newQuantity: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, newQuantity)
        }

        val selection = "$COLUMN_PRODUCT_ID = ? AND $COLUMN_SIZE = ? AND $COLUMN_COLOR = ?"
        val selectionArgs = arrayOf(productId, size, color)

        val rowsAffected = db.update(TABLE_CART, values, selection, selectionArgs)
        db.close()
        return rowsAffected > 0
    }

    @Synchronized
    fun createOrder(): String {
        val cartItems = getCartItems()
        
        if (cartItems.isEmpty()) {
            throw IllegalStateException("Cart is empty")
        }

        val orderId = System.currentTimeMillis().toString()
        val total = cartItems.sumOf { it.price * it.quantity }

        // Insert order
        val orderValues = ContentValues().apply {
            put(COLUMN_ORDER_ID, orderId)
            put(COLUMN_ORDER_DATE, System.currentTimeMillis())
            put(COLUMN_ORDER_STATUS, "pending")
            put(COLUMN_ORDER_TOTAL, total)
        }

        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                db.insert(TABLE_ORDERS, null, orderValues)
                db.delete(TABLE_CART, null, null) // Clear cart directly
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        return orderId
    }

    fun getOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = this.readableDatabase
        
        val cursor = db.query(
            TABLE_ORDERS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ORDER_DATE DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val order = Order(
                    id = getString(getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                    date = getLong(getColumnIndexOrThrow(COLUMN_ORDER_DATE)),
                    status = getString(getColumnIndexOrThrow(COLUMN_ORDER_STATUS)),
                    total = getDouble(getColumnIndexOrThrow(COLUMN_ORDER_TOTAL))
                )
                orders.add(order)
            }
        }
        cursor.close()
        db.close()
        return orders
    }
}

data class CartItem(
    val id: Int,
    val productId: String,
    val name: String,
    val price: Double,
    val size: String,
    val color: String,
    val quantity: Int,
    val imageUrl: String?
)
