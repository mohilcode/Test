package com.example.test.api

import android.util.Log
import com.example.test.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AsinAPIClient {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    suspend fun fetchASINApiKey(): String? = withContext(Dispatchers.IO) {
        val encryptUtil = EncryptUtil

        val decryptedUrl = BuildConfig.WUpy2M3lmyPQtMj2LyFBdPRT.let { encryptUtil.decrypt(it) }
        val decryptedToken = BuildConfig.YMNAIjPwiClJnEkLqUzbLTUkM.let { encryptUtil.decrypt(it) }

        val apiUrl = "$decryptedUrl/ASIN_API_KEY"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        connection.setRequestProperty("Authorization", decryptedToken)

        try {
            connection.inputStream.bufferedReader().use {
                val json = it.readText()
                val jsonObject = JSONObject(json)
                return@withContext jsonObject.getString("api_key")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        return@withContext null
    }

    suspend fun fetchProductInfo(barcode: String): AsinProductResponse? = withContext(Dispatchers.IO) {

        val apiKey = fetchASINApiKey() ?: return@withContext null
        val baseUrl = "https://api.asindataapi.com"

        val searchUrl = "$baseUrl/request?api_key=$apiKey&type=search&amazon_domain=amazon.co.jp&search_term=$barcode"
        val searchResponse = performRequest(searchUrl)
        Log.d("ASIN", "Asin: $searchResponse")
        if (searchResponse != null) {
            if (searchResponse.has("search_results")) {
                val searchResults = searchResponse.getJSONArray("search_results")
                if (searchResults.length() > 0) {
                    val asin = searchResults.getJSONObject(0).getString("asin")
                    Log.d("ASIN", "Asin: $asin")
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
            Log.e("ASINAPIClient", "Error occurred while fetching product info", e)
            e.printStackTrace()
        }

        return null
    }

}
