package com.turahan.dev.volunteer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityVolDonasiBerhasilBinding
import com.turahan.dev.user.MainActivity

class VolDonasiBerhasil : AppCompatActivity() {

    private lateinit var binding: ActivityVolDonasiBerhasilBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolDonasiBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")
        databaseUser = FirebaseDatabase.getInstance().getReference("User")

        binding.backButton.setOnClickListener {
            val intent = Intent(this, VolunteerMain::class.java)
            startActivity(intent)
            finish()
        }

        var idDonasi = intent.getStringExtra("idDonasi")
        val judulDonasi = intent.getStringExtra("judulDonasi")
        val tanggalDonasi = intent.getStringExtra("tanggalDonasi")
        val jenisDonasi = intent.getStringExtra("jenisDonasi")
        val alamatDonasi = intent.getStringExtra("alamatDonasi")

        binding.tvTanggal.text = tanggalDonasi
        binding.tvJenisDonasi.text = jenisDonasi
        binding.tvIdDonasi.text = idDonasi
        binding.tvStatus.text = "Success"
        binding.tvJudulDonasi.text = judulDonasi
        binding.tvAlamatDonasi.text = alamatDonasi
    }
}