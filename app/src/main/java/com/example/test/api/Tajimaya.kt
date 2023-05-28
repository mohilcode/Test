package com.example.test.api

interface TajimayaAPI {
    suspend fun fetchProductInfo(barcodeValue: String): TajimayaResponse?
}
