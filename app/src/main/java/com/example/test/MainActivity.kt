package com.example.test

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.test.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.bottomNavigation.visibility = View.INVISIBLE
        binding.bottomNavigation.postDelayed({
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomNavigation.startAnimation(fadeInAnimation)
        }, 250)

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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

        binding.anime.setOnClickListener {
            startBarcodeScan()
        }
    }

}

