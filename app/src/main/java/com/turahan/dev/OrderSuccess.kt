package com.turahan.dev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.turahan.dev.databinding.ActivityOrderSuccessBinding
import com.turahan.dev.user.MainActivity
import com.turahan.dev.user.profile.DonationHistory

class OrderSuccess : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToHomeButton.setOnClickListener {
            val intentMain = Intent(this, MainActivity::class.java)
            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentMain)
            finish()
        }
    }
}