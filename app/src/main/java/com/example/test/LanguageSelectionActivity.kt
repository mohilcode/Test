package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityLanguageSelectionBinding

class LanguageSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageSelectionBinding
    private var selectedLanguage: String? = null
    private val typingMessage = "Select a Language"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val buttons = listOf(
            binding.englishButton,
            binding.spanishButton,
            binding.chineseButton,
            binding.koreanButton,
            binding.hindiButton,
            binding.russianButton,
            binding.thaiButton,
            binding.arabicButton
        )

        buttons.forEach { it.visibility = View.INVISIBLE }
        binding.animationView.visibility = View.INVISIBLE

        binding.animationView.postDelayed({
            binding.animationView.apply {
                visibility = View.VISIBLE
                startAnimation(fadeInAnimation)
            }
            buttons.forEach {
                it.apply {
                    visibility = View.VISIBLE
                    startAnimation(fadeInAnimation)
                }
            }
        }, 500)

        binding.typingTextView.postDelayed({
            binding.typingTextView.visibility = View.VISIBLE
            binding.typingTextView.startTypingText(typingMessage)
        }, 1000)

        val languageMap = mapOf(
            binding.englishButton to "English",
            binding.spanishButton to "Español",
            binding.chineseButton to "中文 (简体)",
            binding.hindiButton to "हिन्दी",
            binding.koreanButton to "한국어",
            binding.russianButton to "Русский",
            binding.thaiButton to "ไทย",
            binding.arabicButton to "العربية"
        )

        for ((button, language) in languageMap) {
            button.setOnClickListener {
                saveLanguagePreference(language)
                selectedLanguage = language

                buttons.forEach { it.isSelected = false }
                button.isSelected = true
            }
        }

        binding.nextButton.setOnClickListener {
            if (selectedLanguage != null) {
                navigateToMainActivity()
                overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out)
            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.typingTextView.stopTyping()
    }

    private fun saveLanguagePreference(language: String) {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("selected_language", language)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
