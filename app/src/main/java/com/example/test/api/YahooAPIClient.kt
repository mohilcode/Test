package com.example.test.api

import android.util.Log
import com.example.test.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class YahooAPIClient {

    suspend fun fetchProductInfo(barcodeValue: String): YahooResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.YAHOO_API_KEY
        val apiUrl = "https://shopping.yahooapis.jp/ShoppingWebService/V3/itemSearch?appid=$apiKey&jan_code=$barcodeValue&results=1"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.inputStream.bufferedReader().use {
                val json = it.readText()
                val jsonObject = JSONObject(json)

                val hits = jsonObject.getJSONArray("hits")
                if (hits.length() > 0) {
                    val firstItem = hits.getJSONObject(0)
                    val itemName = firstItem.getString("name")
                    val itemCaption = firstItem.getString("description")
                    return@withContext YahooResponse(itemName, itemCaption)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        return@withContext null
    }

}
