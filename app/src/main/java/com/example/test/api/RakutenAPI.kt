package com.example.test.api

interface RakutenAPI {
    suspend fun fetchProductInfo(barcodeValue: String): RakutenResponse?
}
