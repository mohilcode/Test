package com.example.test.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleImageSearchApi: GoogleImageSearchApi = retrofit.create(GoogleImageSearchApi::class.java)

}