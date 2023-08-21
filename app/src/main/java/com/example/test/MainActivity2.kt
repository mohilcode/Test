package com.example.test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import com.example.test.databinding.ActivityMain2Binding
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.test.api.GoogleImageSearchApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import android.view.animation.AnimationUtils

class MainActivity2 : BaseActivity() {
    private lateinit var binding: ActivityMain2Binding
    private var currentImageIndex = 0
    private var imageUrls: List<Pair<String, String>> = emptyList()
    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private val typingMessage = "Is this your product?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.typingTextView.visibility = View.INVISIBLE
        binding.animationView1.visibility = View.INVISIBLE
        binding.imageView.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.buttonYes.visibility = View.INVISIBLE
        binding.buttonNo.visibility = View.INVISIBLE
        binding.animationView2.visibility = View.INVISIBLE
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

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
                    // Start the LanguageSelectionActivity when the language item is clicked
                    startActivity(Intent(this, LanguageMenuActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val barcodeValue = intent.getStringExtra("barcode_value")

        binding.buttonYes.setOnClickListener {
            val selectedImageUrl = imageUrls.getOrNull(currentImageIndex - 1)
            if (selectedImageUrl != null) {
                val intent = Intent(this@MainActivity2, ProductInfoActivity::class.java)
                intent.putExtra("image_url", selectedImageUrl.second)
                intent.putExtra("actual_image_url", selectedImageUrl.first)
                intent.putExtra("barcode_value", barcodeValue)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out)
            }

        }

        binding.buttonNo.setOnClickListener {
            showNextImage()
        }

        searchAndDisplayImage(barcodeValue)
    }

    private fun searchAndDisplayImage(query: String?) {
        if (query == null) return

        lifecycleScope.launch {
            GoogleImageSearchApiClient.searchImages(query) { imageUrls, error ->
                if (error == null) {
                    this@MainActivity2.imageUrls = imageUrls ?: emptyList()
                    if (imageUrls.isNullOrEmpty()) {
                        displayNoImagesFound()
                    } else {
                        showNextImage()
                    }
                } else {
                    Toast.makeText(this@MainActivity2, "Error: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    binding.animationView.visibility = View.INVISIBLE  // Make the Lottie animation invisible if there's an error
                }
            }
        }
    }


    private fun resetTypingTextView() {
        binding.typingTextView.reset()
        binding.typingTextView.visibility = View.INVISIBLE
    }

    private fun displayNoImagesFound() {
        // Make every other view invisible
        binding.typingTextView.visibility = View.INVISIBLE
        binding.animationView1.visibility = View.INVISIBLE
        binding.animationView.visibility = View.INVISIBLE
        binding.imageView.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.buttonYes.visibility = View.INVISIBLE
        binding.buttonNo.visibility = View.INVISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.typingTextView1.text = ""  // Reset the text
            binding.typingTextView1.visibility = View.VISIBLE
            binding.typingTextView1.startAnimation(fadeInAnimation)
            binding.typingTextView1.startTypingText("I'm sorry, I couldn't find your product :(")

            binding.animationView2.visibility = View.VISIBLE
            binding.animationView2.playAnimation()
        }, 500)
    }


    private fun showNextImage() {
        resetTypingTextView()
        if (imageUrls.isNotEmpty() && currentImageIndex < imageUrls.size) {
            // Set the animation view to visible and start the animation
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.playAnimation()

            // Make other views invisible
            binding.typingTextView.visibility = View.INVISIBLE
            binding.imageView.visibility = View.INVISIBLE
            binding.buttonYes.visibility = View.INVISIBLE
            binding.buttonNo.visibility = View.INVISIBLE
            binding.imageView2.visibility = View.INVISIBLE
            binding.animationView1.visibility = View.INVISIBLE

            Picasso.get()
                .load(imageUrls[currentImageIndex].first)
                .into(binding.imageView, object : Callback {
                    override fun onSuccess() {
                        // Hide the animation and show the other UI components
                        binding.animationView.visibility = View.INVISIBLE
                        binding.imageView.visibility = View.VISIBLE
                        binding.imageView.startAnimation(fadeInAnimation)
                        binding.imageView2.visibility = View.VISIBLE
                        binding.imageView2.startAnimation(fadeInAnimation)
                        binding.buttonYes.visibility = View.VISIBLE
                        binding.buttonYes.startAnimation(fadeInAnimation)
                        binding.buttonNo.visibility = View.VISIBLE
                        binding.buttonNo.startAnimation(fadeInAnimation)
                        binding.bottomNavigation.startAnimation(fadeInAnimation)
                        binding.animationView1.visibility = View.VISIBLE
                        binding.animationView1.startAnimation(fadeInAnimation)
                        binding.typingTextView.postDelayed({
                            binding.typingTextView.visibility = View.VISIBLE
                            binding.typingTextView.startTypingText(typingMessage)
                        }, 1000)
                    }

                    override fun onError(e: Exception?) {
                        currentImageIndex++
                        showNextImage()
                    }
                })
            currentImageIndex++
        } else {
            displayNoImagesFound()
        }
    }


}


