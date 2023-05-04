package com.example.test.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleCustomSearchApi {
    @GET("v1")
    fun searchImages(
        @Query("key") apiKey: String,
        @Query("cx") searchEngineId: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = "image",
//        @Query("num") numResults: Int = 1
    ): Call<ImageSearchResponse>
}
