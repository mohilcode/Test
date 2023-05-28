package com.example.test.api

interface YahooAPIAPI {
    suspend fun fetchProductInfo(barcodeValue: String): YahooResponse?
}