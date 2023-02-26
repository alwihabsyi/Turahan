package com.turahan.dev.sign

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.turahan.dev.databinding.ActivityFirstTimerBinding
import com.turahan.dev.onboarding.OnBoarding

class FirstTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.imageView.alpha = 0f
        binding.textView.alpha = 0f
        binding.textView2.alpha = 0f

        binding.imageView.animate().alpha(1f).duration = 1500
        binding.textView.animate().alpha(1f).duration = 1500
        binding.textView2.animate().apply {
            duration = 1500
            alpha(1f)
        }.withEndAction {
            if (onBoardingFinished()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, OnBoarding::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}