package com.example.test.api

data class AsinProductResponse(
    val product: Product
)

data class Product(
    val title: String,
    val description: String
)

