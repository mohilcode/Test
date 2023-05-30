package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.test.api.RakutenAPIClient
import com.example.test.api.RakutenResponse
import com.example.test.api.YahooAPIClient
import com.example.test.api.YahooResponse
import com.example.test.databinding.ActivityProductInfoBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import com.example.test.api.AsinAPIClient
import com.example.test.api.AsinProductResponse
import com.example.test.api.AsinSearchResponse
import com.example.test.api.GptApiClient
import com.example.test.api.TajimayaAPIClient
import com.example.test.api.TajimayaResponse


class ProductInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductInfoBinding
    private val rakutenApiClient = RakutenAPIClient()
    private val yahooApiClient = YahooAPIClient()
    private val asinApiClient = AsinAPIClient()
    private val tajimayaApiClient = TajimayaAPIClient()
    private val gptApiClient = GptApiClient()
    private var imageUrl: String? = null
    private var isFetchSuccessful = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUrl = intent.getStringExtra("image_url")
        val actualImageUrl = intent.getStringExtra("actual_image_url")
        val barcodeValue = intent.getStringExtra("barcode_value")

        if (actualImageUrl != null) {
            Picasso.get()
                .load(actualImageUrl)
                .into(binding.productImageView)
        }

        binding.progressBar.visibility = View.VISIBLE
        fetchProductInfo(barcodeValue ?: "")
    }

    private fun fetchProductInfo(barcodeValue: String) {
        lifecycleScope.launch {
            val rakutenResponse = rakutenApiClient.fetchProductInfo(barcodeValue)
            if (rakutenResponse != null) {
                displayProductInfo(rakutenResponse)
            } else {
                val yahooResponse = yahooApiClient.fetchProductInfo(barcodeValue)
                if (yahooResponse != null) {
                    displayProductInfo(yahooResponse)
                } else {
                    val tajimayaResponse = tajimayaApiClient.fetchProductInfo(barcodeValue)
                    if (tajimayaResponse != null) {
                        displayProductInfo(tajimayaResponse)
                    } else {
                        val asinResponse = asinApiClient.fetchProductInfo(barcodeValue)
                        displayProductInfo(asinResponse)
                    }
                }
            }
        }
    }

    private fun displayProductInfo(response: Any?) {
        response?.let {
            isFetchSuccessful = true
            val (productName, productDescription) = when (it) {
                is RakutenResponse -> Pair(it.itemName, it.itemCaption)
                is YahooResponse -> Pair(it.name, it.description)
                is TajimayaResponse -> {
                    Log.d("ProductInfoActivity", "Tajimaya response: ${it.productName}, ${it.productDescription}")
                    Pair(it.productName, it.productDescription)
                }
                is AsinProductResponse -> Pair(it.product.title, it.product.description)
                else -> return
            }
            lifecycleScope.launch {
                val gptResponse = gptApiClient.translate("$productName: $productDescription")
                if (gptResponse != null) {
                    binding.productNameTextView.text = "Product name: $productName"
                    binding.productDescriptionTextView.text = "Product description: ${gptResponse.translatedText}"
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}
