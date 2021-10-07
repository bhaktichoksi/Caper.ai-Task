package com.example.scannerdb.Model


data class ProductModel(
    val id: Int = -1,
    val productId:String,
    val qrUrl: String,
    val thumbnail: String,
    val name: String,
    val price:String

)