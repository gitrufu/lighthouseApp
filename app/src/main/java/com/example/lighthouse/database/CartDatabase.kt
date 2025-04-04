package com.example.lighthouse.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CartDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "lighthouse.db"
        private const val DATABASE_VERSION = 1

        // Table name
        const val TABLE_CART = "cart_items"

        // Column names
        const val COLUMN_ID = "id"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_IMAGE_RES_ID = "image_res_id"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_SIZE = "size"
        const val COLUMN_COLOR = "color"
        const val COLUMN_ADDED_AT = "added_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create cart items table
        val CREATE_CART_TABLE = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_ID TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_IMAGE_RES_ID INTEGER NOT NULL,
                $COLUMN_QUANTITY INTEGER NOT NULL,
                $COLUMN_SIZE TEXT NOT NULL,
                $COLUMN_COLOR TEXT NOT NULL,
                $COLUMN_ADDED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(CREATE_CART_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        // Create tables again
        onCreate(db)
    }
}
