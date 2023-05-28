package com.example.test.api

import com.example.test.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

object GoogleImageSearchApiClient {

    private const val BASE_URL = "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleImageSearchApi: GoogleImageSearchApi = retrofit.create(GoogleImageSearchApi::class.java)

    fun searchImages(query: String, callback: (List<Pair<String, String>>?, Throwable?) -> Unit) {
        val apiKey = BuildConfig.GOOGLE_API_KEY
        val searchEngineId = BuildConfig.GOOGLE_ENGINE_ID

        googleImageSearchApi.searchImages(apiKey, searchEngineId, query).enqueue(object : Callback<ImageSearchResponse> {
            override fun onResponse(call: Call<ImageSearchResponse>, response: Response<ImageSearchResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val imageUrls = response.body()?.items?.map { Pair(it.imageUrl, it.image.source) } ?: emptyList()
                    callback(imageUrls, null)
                } else {
                    callback(null, Exception("Failed to fetch image."))
                }
            }

            override fun onFailure(call: Call<ImageSearchResponse>, t: Throwable) {
                callback(null, t)
            }
        })
    }
}
