package com.example.test.api

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.test.BuildConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launch the coroutine on the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            fetchDataFromServer()
        }
    }

    private suspend fun fetchDataFromServer() {
        val encryptUtil = EncryptUtil

        val decryptedUrl = BuildConfig.WUpy2M3lmyPQtMj2LyFBdPRT.let { encryptUtil.decrypt(it) }
        val decryptedToken = BuildConfig.YMNAIjPwiClJnEkLqUzbLTUkM.let { encryptUtil.decrypt(it) }

        val apiUrl = "$decryptedUrl/GOOGLE_ENGINE_ID"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Authorization", decryptedToken)

        var apiKey: String? = null
        try {
            connection.inputStream.bufferedReader().use {
                val json = it.readText()
                val jsonObject = JSONObject(json)
                apiKey = jsonObject.getString("api_key")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        // Switch to the main thread to log the data
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("JSON_TAG", "API Key: $apiKey")
        }
    }
}