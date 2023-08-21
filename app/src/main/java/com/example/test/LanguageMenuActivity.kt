package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.test.databinding.ActivityLanguageMenuBinding


class LanguageMenuActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguageMenuBinding
    private var selectedLanguage: String? = null
    private val typingMessage = "Select a Language"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        binding.bottomNavigation.visibility = View.INVISIBLE
        binding.bottomNavigation.postDelayed({
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomNavigation.startAnimation(fadeInAnimation)
        }, 500)

        val buttons = listOf(
            binding.englishButton,
            binding.spanishButton,
            binding.chineseButton,
            binding.koreanButton,
            binding.hindiButton,
            binding.russianButton,
            binding.thaiButton,
            binding.arabicButton,
            binding.nextButton
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

        val savedLanguage = getSavedLanguagePreference()
        if (savedLanguage != null) {
            val selectedButton = languageMap.entries.firstOrNull { it.value == savedLanguage }?.key
            selectedButton?.let {
                it.isSelected = true
                selectedLanguage = savedLanguage
            }
        }

        binding.nextButton.setOnClickListener {
            if (selectedLanguage != null) {
                finish()
                overridePendingTransition(R.anim.fade_in_new, R.anim.fade_out)
            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_language
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
                    true
                }
                else -> false
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

    private fun getSavedLanguagePreference(): String? {
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("selected_language", null)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}