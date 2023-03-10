package com.turahan.dev.user.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityDonasiBerhasilBinding
import com.turahan.dev.user.MainActivity

class DonasiBerhasil : AppCompatActivity() {

    private lateinit var binding: ActivityDonasiBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonasiBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        var idDonasi = intent.getStringExtra("idDonasi")
        val judulDonasi = intent.getStringExtra("judulDonasi")
        val tanggalDonasi = intent.getStringExtra("tanggalDonasi")
        val jenisDonasi = intent.getStringExtra("jenisDonasi")
        val alamatDonasi = intent.getStringExtra("alamatDonasi")
        val kategoriDonasi = intent.getStringExtra("kategoriDonasi")

        if(alamatDonasi == "Cash"){
            binding.tvCategory.text = "Donation Amount"
        }

        binding.tvTanggal.text = tanggalDonasi
        binding.tvJenisDonasi.text = jenisDonasi
        binding.tvIdDonasi.text = idDonasi
        binding.tvStatus.text = "Success"
        binding.tvJudulDonasi.text = judulDonasi
        binding.tvAlamatDonasi.text = alamatDonasi
        binding.tvKategoriDonasi.text = kategoriDonasi
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}