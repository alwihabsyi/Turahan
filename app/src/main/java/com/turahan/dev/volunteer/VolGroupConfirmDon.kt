package com.turahan.dev.volunteer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityVolGroupConfirmDonBinding

class VolGroupConfirmDon : AppCompatActivity() {

    private lateinit var binding: ActivityVolGroupConfirmDonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolGroupConfirmDonBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}