package com.turahan.dev.user.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityDonasiBerhasilBinding
import com.turahan.dev.user.MainActivity

class DonasiBerhasil : AppCompatActivity() {

    private lateinit var binding: ActivityDonasiBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonasiBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.expandButton.setOnClickListener {
            if (binding.containerDetalJumlahDonasi.visibility == View.VISIBLE) {
                binding.containerDetalJumlahDonasi.visibility = View.GONE
                binding.expandButton.setImageResource(R.drawable.expand_more)
                TransitionManager.beginDelayedTransition(binding.cvBuktiCampaign, AutoTransition())
            } else {
                TransitionManager.beginDelayedTransition(binding.cvBuktiCampaign, AutoTransition())
                binding.containerDetalJumlahDonasi.visibility = View.VISIBLE
                binding.expandButton.setImageResource(R.drawable.expand_less)
            }
        }

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
        val judulCampaign = intent.getStringExtra("judulCampaign")
        val buktiFoto = intent.getStringExtra("buktiFoto")

        if(alamatDonasi == "Cash"){
            binding.tvCategory.text = "Donation Amount"
        }

        if(judulCampaign == null){
            binding.containerJumlahDonasi.visibility = View.GONE
        }

        binding.tvTanggal.text = tanggalDonasi
        binding.tvJenisDonasi.text = jenisDonasi
        binding.tvIdDonasi.text = idDonasi
        binding.tvStatus.text = "Success"
        binding.tvJudulDonasi.text = judulDonasi
        binding.tvAlamatDonasi.text = alamatDonasi
        binding.tvKategoriDonasi.text = kategoriDonasi
        binding.tvJudulCampaign.text = judulCampaign

        if (buktiFoto != "") {
            Picasso
                .get()
                .load(buktiFoto)
                .into(binding.ivFotoCampaign)
        } else {
            binding.ivFotoCampaign.setImageResource(R.drawable.ic_campaign)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}