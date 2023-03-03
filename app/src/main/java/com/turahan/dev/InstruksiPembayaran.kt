package com.turahan.dev

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.turahan.dev.databinding.ActivityInstruksiPembayaranBinding
import com.turahan.dev.user.MainActivity

class InstruksiPembayaran : AppCompatActivity() {

    private lateinit var binding: ActivityInstruksiPembayaranBinding
    var random = (100..999).random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstruksiPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.expandButton.setOnClickListener {
            if (binding.containerDetalJumlahDonasi.visibility == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                }
                binding.containerDetalJumlahDonasi.visibility = View.GONE
                binding.expandButton.setImageResource(R.drawable.expand_more)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                }
                binding.containerDetalJumlahDonasi.visibility = View.VISIBLE
                binding.expandButton.setImageResource(R.drawable.expand_less)
            }
        }
    }
}