package com.example.test.api

import android.util.Log
import com.example.test.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

object GoogleImageSearchApiClient {

    suspend fun fetchGoogleApiKey(): String? = withContext(Dispatchers.IO) {
        val encryptUtil = EncryptUtil

        val decryptedUrl = BuildConfig.WUpy2M3lmyPQtMj2LyFBdPRT.let { encryptUtil.decrypt(it) }
        val decryptedToken = BuildConfig.YMNAIjPwiClJnEkLqUzbLTUkM.let { encryptUtil.decrypt(it) }

        val apiUrl = "$decryptedUrl/GOOGLE_API_KEY"
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

    suspend fun fetchGoogleEngineKey(): String? = withContext(Dispatchers.IO) {
        val encryptUtil = EncryptUtil

        val decryptedUrl = BuildConfig.WUpy2M3lmyPQtMj2LyFBdPRT.let { encryptUtil.decrypt(it) }
        val decryptedToken = BuildConfig.YMNAIjPwiClJnEkLqUzbLTUkM.let { encryptUtil.decrypt(it) }

        val apiUrl = "$decryptedUrl/GOOGLE_ENGINE_ID"
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

    private const val BASE_URL = "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleImageSearchApi: GoogleImageSearchApi = retrofit.create(GoogleImageSearchApi::class.java)

    suspend fun searchImages(query: String, callback: (List<Pair<String, String>>?, Throwable?) -> Unit) {
        val apiKey = fetchGoogleApiKey() ?: return
        val searchEngineId = fetchGoogleEngineKey() ?: return

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
