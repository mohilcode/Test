package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.databinding.ActivityMain2Binding
import android.widget.Toast
import com.example.test.api.ApiClient
import com.example.test.api.ImageSearchResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private var currentImageIndex = 0
    private var imageUrls: List<Pair<String, String>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val barcodeValue = intent.getStringExtra("barcode_value")

        binding.buttonYes.setOnClickListener {
            val selectedImageUrl = imageUrls.getOrNull(currentImageIndex - 1)
            if (selectedImageUrl != null) {
                val intent = Intent(this@MainActivity2, ProductInfoActivity::class.java)
                intent.putExtra("image_url", selectedImageUrl.second)
                startActivity(intent)
            }
        }

        binding.buttonNo.setOnClickListener {
            // Handle the "No" button click
            showNextImage()
        }

        searchAndDisplayImage(barcodeValue)
    }

    private fun searchAndDisplayImage(query: String?) {
        if (query == null) return

        val apiKey = "API KEY"
        val searchEngineId = "ENGINE ID"

        ApiClient.googleImageSearchApi.searchImages(apiKey, searchEngineId, query).enqueue(object : Callback<ImageSearchResponse> {
            override fun onResponse(call: Call<ImageSearchResponse>, response: Response<ImageSearchResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    imageUrls = response.body()?.items?.map { Pair(it.imageUrl, it.image.source) } ?: emptyList()
                    showNextImage()
                } else {
                    Toast.makeText(this@MainActivity2, "Failed to fetch image.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageSearchResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity2, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showNextImage() {
        if (imageUrls.isNotEmpty() && currentImageIndex < imageUrls.size) {
            Picasso.get()
                .load(imageUrls[currentImageIndex].first)
                .into(binding.imageView)
            currentImageIndex++
        } else {
            Toast.makeText(this@MainActivity2, "No more images found.", Toast.LENGTH_SHORT).show()
        }
    }
}
