package com.example.test.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleImageSearchApi {

    @GET("customsearch/v1")
    fun searchImages(
        @Query("key") apiKey: String,
        @Query("cx") searchEngineId: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = "image",
    ): Call<ImageSearchResponse>

}