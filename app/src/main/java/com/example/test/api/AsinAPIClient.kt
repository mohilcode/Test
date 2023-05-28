package com.example.test.api

import android.util.Log
import com.example.test.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class AsinAPIClient {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    suspend fun fetchProductInfo(barcode: String): AsinProductResponse? = withContext(Dispatchers.IO) {

        val apiKey = BuildConfig.ASIN_API_KEY
        val baseUrl = "https://api.asindataapi.com"

        val searchUrl = "$baseUrl/request?api_key=$apiKey&type=search&amazon_domain=amazon.co.jp&search_term=$barcode"
        val searchResponse = performRequest(searchUrl)

        if (searchResponse != null) {
            if (searchResponse.has("search_results")) {
                val searchResults = searchResponse.getJSONArray("search_results")
                if (searchResults.length() > 0) {
                    val asin = searchResults.getJSONObject(0).getString("asin")
                    val productUrl =
                        "$baseUrl/request?api_key=$apiKey&type=product&amazon_domain=amazon.co.jp&asin=$asin"

                    val productResponse = performRequest(productUrl)
                    if (productResponse != null) {

                        if (productResponse.has("product")) {
                            val product = productResponse.getJSONObject("product")
                            val itemName = product.getString("title")
                            val itemCaption = product.getString("description")
                            return@withContext AsinProductResponse(Product(itemName, itemCaption))
                        }
                    }
                }
            }
        }
        return@withContext null
    }

    private suspend fun performRequest(url: String): JSONObject? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        return JSONObject(responseBody)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}
