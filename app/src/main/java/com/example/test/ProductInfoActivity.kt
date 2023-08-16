package com.example.test

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.example.test.api.GptApiClient
import com.example.test.api.TajimayaAPIClient
import com.example.test.api.TajimayaResponse
import androidx.browser.customtabs.CustomTabsIntent
import android.net.Uri



class ProductInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductInfoBinding
    private val rakutenApiClient = RakutenAPIClient()
    private val yahooApiClient = YahooAPIClient()
    private val asinApiClient = AsinAPIClient()
    private val tajimayaApiClient = TajimayaAPIClient()
    private val gptApiClient = GptApiClient()
    private var isFetchSuccessful = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("image_url")
        val actualImageUrl = intent.getStringExtra("actual_image_url")
        val barcodeValue = intent.getStringExtra("barcode_value")
        val translatedUrl = "https://translate.google.com/translate?hl=en&sl=auto&tl=en&u=${Uri.encode(imageUrl)}"

        if (actualImageUrl != null) {
            Picasso.get()
                .load(actualImageUrl)
                .into(binding.productImageView)
        }

        binding.progressBar.visibility = View.VISIBLE

        binding.learnMore.setOnClickListener {
            openCustomTabOrFallback(translatedUrl)
        }

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
                    }  else {
                            handleFailedFetch()
                        }
                }
            }
        }
    }

//else {
//    val asinResponse = asinApiClient.fetchProductInfo(barcodeValue)
//    if (asinResponse != null) {
//        displayProductInfo(asinResponse)
//    }
//}

    private fun isChromeCustomTabsSupported(context: Context, url: String): Boolean {
        val intent = Intent("android.intent.action.VIEW", Uri.parse(url))
        val packageManager = context.packageManager
        val resolvedActivities = packageManager.queryIntentActivities(intent, 0)
        for (info in resolvedActivities) {
            if (info.activityInfo.packageName == "com.android.chrome") {
                return true
            }
        }
        return false
    }

    private fun openCustomTabOrFallback(url: String?) {
        url?.let {
            if (isChromeCustomTabsSupported(this, url)) {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            } else {
                // Fallback to the default browser
                openWebView(url)
            }
        }
    }



//    private fun openCustomTab(url: String?) {
//        url?.let {
//            val customTabsIntent = CustomTabsIntent.Builder().build()
//            customTabsIntent.launchUrl(this, Uri.parse(it))
//        }
//    }

    private fun handleFailedFetch() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Couldn't find information", Toast.LENGTH_LONG).show()
        binding.learnMore.visibility = View.VISIBLE
    }

    private fun openWebView(url: String?) {
        url?.let {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("URL", it)
            startActivity(intent)
        }
    }

    private fun displayProductInfo(response: Any?) {
        response?.let {
            isFetchSuccessful = true
            Toast.makeText(this, "Data retrieved successfully!", Toast.LENGTH_SHORT).show()
            val (productName, productDescription) = when (it) {
                is YahooResponse -> Pair(it.name, it.description)
                is RakutenResponse -> Pair(it.itemName, it.itemCaption)
                is TajimayaResponse -> {
                    Pair(it.productName, it.productDescription)
                }
//                is AsinProductResponse -> Pair(it.product.title, it.product.description)
                else -> return
            }
            lifecycleScope.launch {
                val gptResponse = gptApiClient.translate("$productName: $productDescription")
//                val gptResponse = gptApiClient.summarize("$productName: $productDescription")
                if (gptResponse != null) {
                    binding.productNameTextView.text = "Product name: $productName"
                    binding.productDescriptionTextView.text = "Product description: ${gptResponse.translatedText}"
//                    binding.productDescriptionTextView.text = "Product description: ${gptResponse.summarizedText}"
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}





