package com.example.scannerdb.DataBase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.scannerdb.Model.CartModel
import com.example.scannerdb.Model.ProductModel


import java.util.*




class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val CREATE_PRODUCT_TABLE = ("CREATE TABLE " + TABLE_PRODUCT + "("
            + COLUMN_INDEX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PRODUCT_ID + " TEXT," + COLUMN_PRODUCT_QRCODE + " TEXT,"
            + COLUMN_PRODUCT_IMAGE + " TEXT," + COLUMN_PRODUCT_NAME + " TEXT," + COLUMN_PRODUCT_PRICE + " TEXT"+ ")")

    private val CREATE_CART_TABLE = ("CREATE TABLE " + TABLE_ADD_TO_CART + "("
            + COLUMN_INDEX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PRODUCT_ID + " TEXT," + COLUMN_PRODUCT_QRCODE + " TEXT,"
            + COLUMN_PRODUCT_IMAGE + " TEXT," + COLUMN_PRODUCT_NAME + " TEXT," + COLUMN_PRODUCT_PRICE + " TEXT,"+ COLUMN_PRODUCT_QUANTITY + " TEXT"+")")

    // drop table sql query
    private val DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS $TABLE_PRODUCT"
    private val DROP_CART_TABLE = "DROP TABLE IF EXISTS $TABLE_ADD_TO_CART"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_PRODUCT_TABLE)
        db?.execSQL(CREATE_CART_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_PRODUCT_TABLE)
        db?.execSQL(DROP_CART_TABLE)

        onCreate(db)
    }


    fun clearTable(){
        val db = this.readableDatabase
        db.execSQL("delete from "+ TABLE_PRODUCT)
    }

    fun clearCartTable(){
        val db = this.readableDatabase
        db.execSQL("delete from "+ TABLE_ADD_TO_CART)
    }

    //Fetching All Product

    @SuppressLint("Range")
    fun getAllProduct(): List<ProductModel> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_INDEX_ID,
            COLUMN_PRODUCT_IMAGE,
            COLUMN_PRODUCT_ID,
            COLUMN_PRODUCT_QRCODE,
            COLUMN_PRODUCT_NAME,
            COLUMN_PRODUCT_PRICE


        )

        // sorting orders
        val sortOrder = "$COLUMN_INDEX_ID ASC"
        val productList = ArrayList<ProductModel>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_PRODUCT, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder
        )         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val user = ProductModel(
                    id = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_ID)).toInt(),
                    productId = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID)),
                    qrUrl = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QRCODE)),
                    thumbnail = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE)),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)),
                    price = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_PRICE))

                )

                productList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    //Add Product
    fun addProduct(product: ProductModel) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, product.productId)
        values.put(COLUMN_PRODUCT_QRCODE, product.qrUrl)
        values.put(COLUMN_PRODUCT_IMAGE, product.thumbnail)
        values.put(COLUMN_PRODUCT_NAME, product.name)
        values.put(COLUMN_PRODUCT_PRICE, product.price)

        // Inserting Row
        db.insert(TABLE_PRODUCT, null, values)
        db.close()
    }

    //Update Product
    fun updateProduct(product: ProductModel) {

        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, product.productId)



        // updating row
        db.update(
            TABLE_PRODUCT, values, "$COLUMN_INDEX_ID = ?",
            arrayOf(product.id.toString())
        )
        db.close()
    }

    //Delete Product
    fun deleteProduct(id: String) {

        val db = this.writableDatabase
        // delete product record by id
        db.delete(
            TABLE_PRODUCT, "$COLUMN_INDEX_ID = ?",
            arrayOf(id)
        )
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_PRODUCT + "'")
        db.close()
    }


    //Check user if exist or Not through Email
    fun checkProduct(email: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INDEX_ID)
        val db = this.readableDatabase

        // selection criteria
        val selection = "$COLUMN_PRODUCT_NAME = ?"

        // selection argument
        val selectionArgs = arrayOf(email)

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        val cursor = db.query(
            TABLE_PRODUCT, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null
        )  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    //  region cart table events
    @SuppressLint("Range")
    fun getAllCartProduct(): List<CartModel> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_INDEX_ID,
            COLUMN_PRODUCT_IMAGE,
            COLUMN_PRODUCT_ID,
            COLUMN_PRODUCT_QRCODE,
            COLUMN_PRODUCT_NAME,
            COLUMN_PRODUCT_PRICE,
            COLUMN_PRODUCT_QUANTITY


        )

        // sorting orders
        val sortOrder = "$COLUMN_INDEX_ID ASC"
        val productList = ArrayList<CartModel>()

        val db = this.readableDatabase

        // query the user table
        val cursor = db.query(
            TABLE_ADD_TO_CART, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder
        )         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val user = CartModel(
                    id = cursor.getString(cursor.getColumnIndex(COLUMN_INDEX_ID)).toInt(),
                    productId = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID)),
                    qrUrl = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QRCODE)),
                    thumbnail = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE)),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)),
                    price = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_PRICE)),
                    quantity = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY))

                )

                productList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    //Add Product
    fun addProductToCart(product: CartModel) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, product.productId)
        values.put(COLUMN_PRODUCT_QRCODE, product.qrUrl)
        values.put(COLUMN_PRODUCT_IMAGE, product.thumbnail)
        values.put(COLUMN_PRODUCT_NAME, product.name)
        values.put(COLUMN_PRODUCT_PRICE, product.price)
        values.put(COLUMN_PRODUCT_QUANTITY, product.quantity)

        // Inserting Row
        db.insert(TABLE_ADD_TO_CART, null, values)
        db.close()
    }

    //Update Product
    fun updateProductToCart(product: CartModel) {

        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PRODUCT_QUANTITY, product.quantity)

        // updating row
        db.update(
            TABLE_ADD_TO_CART, values, "$COLUMN_PRODUCT_ID = ?",
            arrayOf(product.productId)
        )
        Log.e("updated","Updated value" + product.quantity )
        db.close()
    }

    //Delete Product
    fun deleteProductToCart(id: String) {

        val db = this.writableDatabase
        // delete product record by id
        db.delete(TABLE_ADD_TO_CART, "$COLUMN_PRODUCT_ID = ?", arrayOf(id))
        db.close()
    }


    //Check user if exist or Not through Email
    fun checkProductToCart(email: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INDEX_ID)
        val db = this.readableDatabase

        // selection criteria
        val selection = "$COLUMN_PRODUCT_ID = ?"

        // selection argument
        val selectionArgs = arrayOf(email)

        // query user table with condition

        val cursor = db.query(
            TABLE_ADD_TO_CART, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null
        )  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }


    //endregion
    companion object {
        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "ProductManager.db"

        // Product
        //table name
        private val TABLE_PRODUCT = "Product"
        private val TABLE_ADD_TO_CART = "Cart"

        // User Table Columns names
        private val COLUMN_INDEX_ID = "index_id"
        private val COLUMN_PRODUCT_ID = "productId"
        private val COLUMN_PRODUCT_QRCODE = "qrUrl"
        private val COLUMN_PRODUCT_IMAGE = "thumbnail"
        private val COLUMN_PRODUCT_NAME = "name"
        private val COLUMN_PRODUCT_PRICE = "price"
        private val COLUMN_PRODUCT_QUANTITY = "quantity"
    }
}
