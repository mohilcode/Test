package com.example.test

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivitySplashBinding
import android.view.animation.AnimationUtils


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val FIRST_TIME_OPEN = "first_time_open"
    private lateinit var sharedPreferences: SharedPreferences

    private val typingMessage = "Hi! I'm Kai, your shopping buddy in Japan. Let's get you started!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_and_translate_up)
        binding.welcome.startAnimation(animation)
        binding.sss.startAnimation(animation)


        binding.animationView.startAnimation(fadeInAnimation)


        binding.typingTextView.visibility = View.INVISIBLE

        binding.typingTextView.postDelayed({
            binding.typingTextView.visibility = View.VISIBLE
            binding.typingTextView.startTypingText(typingMessage)
        }, 1000)



        if (isFirstTimeOpen()) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.letsGoButton.visibility = View.VISIBLE
                binding.letsGoButton.startAnimation(animation)
            }, 2500)


            binding.letsGoButton.setOnClickListener {
                val intent = Intent(this, LanguageSelectionActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out)
                setFirstTimeOpenDone()
                finish()
            }
        } else {
                if (isLanguageSet()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LanguageSelectionActivity::class.java))
                }
                finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.typingTextView.stopTyping()
    }

    private fun isLanguageSet(): Boolean {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.contains("selected_language")
    }

    private fun isFirstTimeOpen(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME_OPEN, true)
    }

    private fun setFirstTimeOpenDone() {
        sharedPreferences.edit().putBoolean(FIRST_TIME_OPEN, false).apply()
    }
}
