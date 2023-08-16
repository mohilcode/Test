package com.example.test.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.util.concurrent.TimeoutException

//class TajimayaAPIClient : TajimayaAPI {
//
//    override suspend fun fetchProductInfo(barcodeValue: String): TajimayaResponse? = withContext(Dispatchers.IO) {
//        Log.d("TajimayaAPIClient", "Fetching product info for barcode: $barcodeValue")
//        val apiUrl = "https://webscrap-production.up.railway.app/scrape?barcode=$barcodeValue"
//
//        val url = URL(apiUrl)
//        val connection = url.openConnection() as HttpURLConnection
//
//        try {
//            connection.inputStream.bufferedReader().use {
//                val json = it.readText()
//                Log.d("TajimayaAPIClient", "Received JSON response: $json")
//                val jsonObject = JSONObject(json)
//
//                val productName = jsonObject.getString("productName")
//                val productDescription = jsonObject.getString("productDescription")
//
//                return@withContext TajimayaResponse(productName, productDescription)
//            }
//        } catch (e: Exception) {
//            Log.e("TajimayaAPIClient", "Error occurred while fetching product info", e)
//            e.printStackTrace()
//        } finally {
//            connection.disconnect()
//        }
//
//        return@withContext null
//    }
//}
class TajimayaAPIClient : TajimayaAPI {

    override suspend fun fetchProductInfo(barcodeValue: String): TajimayaResponse? = withContext(Dispatchers.IO) {
        Log.d("TajimayaAPIClient", "Fetching product info for barcode: $barcodeValue")
        val apiUrl = "https://webscrap-production.up.railway.app/scrape?barcode=$barcodeValue"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                if (responseCode == 408) {
                    Log.e("TajimayaAPIClient", "Request timed out (408).")
                    throw TimeoutException("Request timed out.")
                } else {
                    Log.e("TajimayaAPIClient", "Received HTTP error: $responseCode")
                    throw Exception("HTTP error: $responseCode")
                }
            }

            connection.inputStream.bufferedReader().use {
                val json = it.readText()
                Log.d("TajimayaAPIClient", "Received JSON response: $json")
                val jsonObject = JSONObject(json)

                val productName = jsonObject.getString("productName")
                val productDescription = jsonObject.getString("productDescription")

                return@withContext TajimayaResponse(productName, productDescription)
            }
        } catch (e: TimeoutException) {
            // Handle the timeout exception here, for instance:
            // - Log the error
            // - Return a specific error object if needed
            Log.e("TajimayaAPIClient", "Error: Request timed out.", e)
        } catch (e: Exception) {
            Log.e("TajimayaAPIClient", "Error occurred while fetching product info", e)
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        return@withContext null
    }
}

