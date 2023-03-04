package com.turahan.dev

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.databinding.ActivityInstruksiPembayaranBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

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

        binding.backToHomeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val idDonasi = intent.getStringExtra("idDonasi")
        val paymentMethod = intent.getStringExtra("paymentMethod")
        val nominal = intent.getStringExtra("nominal")

        binding.tvIdDonasi.text = idDonasi
        binding.tvMetodePembayaran.text = paymentMethod
        binding.tvStatus.text = "Pending"
        binding.tvJumlahDonasi.text = nominal

        binding.tvJudulVA.text = "Virtual Account ${paymentMethod}"
    }
}