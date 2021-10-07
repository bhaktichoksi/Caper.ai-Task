package com.example.scannerdb.Model


data class CartModel(
    val id: Int = -1,
    val productId: String,
    val qrUrl: String,
    val thumbnail: String,
    val name: String,
    val price:String,
    val quantity:String

)