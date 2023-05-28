package com.example.test.api

import retrofit2.http.GET
import retrofit2.http.Query

interface AsinAPI {
    @GET("request")
    suspend fun searchByBarcode(
        @Query("api_key") apiKey: String,
        @Query("type") type: String,
        @Query("amazon_domain") amazonDomain: String,
        @Query("search_term") searchTerm: String
    ): AsinSearchResponse

    @GET("request")
    suspend fun getProductInfo(
        @Query("api_key") apiKey: String,
        @Query("type") type: String,
        @Query("amazon_domain") amazonDomain: String,
        @Query("asin") asin: String
    ): AsinProductResponse
}

