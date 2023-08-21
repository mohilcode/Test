package com.example.test

import android.content.Context
import android.content.Intent
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
import com.example.test.api.GptApiClient
import com.example.test.api.TajimayaAPIClient
import com.example.test.api.TajimayaResponse
import androidx.browser.customtabs.CustomTabsIntent
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class ProductInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityProductInfoBinding
    private val rakutenApiClient = RakutenAPIClient()
    private val yahooApiClient = YahooAPIClient()
    private val asinApiClient = AsinAPIClient()
    private val tajimayaApiClient = TajimayaAPIClient()
    private val gptApiClient = GptApiClient()
    private var isFetchSuccessful = false
    private var chosenLanguage: String?  = null
    private lateinit var fadeInAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.productNameTextView.visibility = View.INVISIBLE
        binding.productDescriptionTextView.visibility = View.INVISIBLE
        binding.productImageView.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.learnMore.visibility = View.INVISIBLE
        binding.animationView.visibility = View.INVISIBLE
        binding.typingTextView2.visibility = View.INVISIBLE
        binding.animationView1.visibility = View.INVISIBLE
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)



        Log.d("lang", "lang: ${getLanguagePreference()}")
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_scan -> {
                    startBarcodeScan()
                    true
                }
                R.id.nav_language -> {
                    startActivity(Intent(this, LanguageMenuActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val imageUrl = intent.getStringExtra("image_url")
        val actualImageUrl = intent.getStringExtra("actual_image_url")
        val barcodeValue = intent.getStringExtra("barcode_value")
        val languageCodeMapping = mapOf(
            "English" to "en",
            "Español" to "es",
            "Русский" to "ru",
            "ไทย" to "th",
            "中文 (简体)" to "zh-CN",
            "한국어" to "ko",
            "العربية" to "ar",
            "हिन्दी" to "hi"
        )
        val languageCodeMapNew = mapOf(
            "English" to "English",
            "Español" to "Spanish",
            "Русский" to "Russian",
            "ไทย" to "Thai",
            "中文 (简体)" to "Chinese (Simplified) ",
            "한국어" to "korean",
            "العربية" to "arabic",
            "हिन्दी" to "hindi"
        )
        chosenLanguage = languageCodeMapNew[getLanguagePreference()] ?: "en"
        val targetLanguageCode = languageCodeMapping[getLanguagePreference()] ?: "en"
        val translatedUrl = "https://translate.google.com/translate?hl=en&sl=auto&tl=$targetLanguageCode&u=${Uri.encode(imageUrl)}"
        if (actualImageUrl != null) {
            Picasso.get()
                .load(actualImageUrl)
                .into(binding.productImageView)
        }

        binding.animationView.visibility = View.VISIBLE
        binding.typingTextView2.visibility = View.VISIBLE
        binding.typingTextView2.startTypingText("Fetching product Info...")


        binding.learnMore.setOnClickListener {
            openCustomTabOrFallback(translatedUrl)
        }

        fetchProductInfo(barcodeValue ?: "")
    }

    private fun getLanguagePreference(): String {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("selected_language", "English") ?: "English"
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
                openWebView(url)
            }
        }
    }

    private fun handleFailedFetch() {
        binding.animationView.visibility = View.INVISIBLE
        binding.typingTextView2.visibility = View.INVISIBLE
        binding.productNameTextView.visibility = View.INVISIBLE
        binding.productDescriptionTextView.visibility = View.INVISIBLE
        binding.productImageView.visibility = View.VISIBLE
        binding.imageView2.visibility = View.VISIBLE
        binding.animationView1.visibility = View.VISIBLE
        binding.animationView1.startAnimation(fadeInAnimation)
        binding.typingTextView4.startAnimation(fadeInAnimation)
        binding.typingTextView4.startTypingText("Sorry, no info on your product. However, this link might help.")
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
            val (productName, productDescription) = when (it) {
                is YahooResponse -> Pair(it.name, it.description)
                is RakutenResponse -> Pair(it.itemName, it.itemCaption)
                is TajimayaResponse -> {
                    Pair(it.productName, it.productDescription)
                }
                else -> return
            }
            lifecycleScope.launch {

                Log.d("language", "language is $chosenLanguage")
                val gptResponse = chosenLanguage?.let { it1 ->
                    gptApiClient.translate("$productName: $productDescription",
                        it1
                    )
                }
                if (gptResponse != null) {
                    binding.productNameTextView.startTypingText("$productName")
                    binding.productDescriptionTextView.startTypingText("${gptResponse.translatedText}")
                    binding.animationView.visibility = View.INVISIBLE
                    binding.typingTextView2.visibility = View.INVISIBLE
                    binding.productNameTextView.visibility = View.VISIBLE
                    binding.productDescriptionTextView.visibility = View.VISIBLE
                    binding.productImageView.visibility = View.VISIBLE
                    binding.imageView2.visibility = View.VISIBLE
                }
            }
        }
    }
}





