package com.turahan.dev.user.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.data.DataDonasiMakanan
import com.turahan.dev.databinding.ActivityDonationDetailBinding
import com.turahan.dev.user.MainActivity

class DonationDetail : AppCompatActivity() {

    private lateinit var binding: ActivityDonationDetailBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("DonasiMakanan")

        binding.expandButton.setOnClickListener {
            if (binding.containerDetalJumlahDonasi.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                binding.containerDetalJumlahDonasi.visibility = View.GONE
                binding.expandButton.setImageResource(R.drawable.expand_more)
            } else {
                TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                binding.containerDetalJumlahDonasi.visibility = View.VISIBLE
                binding.expandButton.setImageResource(R.drawable.expand_less)
            }
        }

        val user = intent.getParcelableExtra<DataDonasiMakanan>("user")
        //Move Data
        binding.tvMetodePembayaran.text = user?.dropOffPickUp
        binding.tvIdDonasi.text = user?.idDonasi
        binding.tvStatus.text = user?.statusDonasi
        binding.tvJumlahDonasi.text = user?.judulDonasi
        binding.tvDetailKodeUnik.text = user?.kategoriDonasi
        binding.tvAlamatDonasi.text = user?.alamatDonasi

        Picasso
            .get()
            .load(user?.fotoDonasi)
            .into(binding.ivFotoDonasi)

        //Btn Update
        binding.perbaruiStatusButton.setOnClickListener {
            database.child(user?.idDonasi!!).get().addOnSuccessListener {
                val status = it.child("statusDonasi").value.toString()
                if(status == "Pending"){
                    Toast.makeText(this, "Donasi Masih Dalam Proses Konfirmasi", Toast.LENGTH_SHORT).show()
                }else if(status == "Success"){
                    startActivity(Intent(this, DonasiBerhasil::class.java).apply {
                        putExtra("tanggalDonasi", user.tanggalDonasi)
                        putExtra("jenisDonasi", user.dropOffPickUp)
                        putExtra("idDonasi", user.idDonasi)
                        putExtra("statusDonasi", user.statusDonasi)
                        putExtra("judulDonasi", user.judulDonasi)
                        putExtra("alamatDonasi", user.alamatDonasi)
                    })
                    finish()
                }
            }
        }

        //Btn Cancel
        binding.btnCancel.setOnClickListener {
            database.child(user?.idDonasi!!).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Berhasil Cancel Donasi", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        database = FirebaseDatabase.getInstance().getReference("DonasiMakanan")
        val user = intent.getParcelableExtra<DataDonasiMakanan>("user")
        database.child(user?.idDonasi!!).get().addOnSuccessListener {
            val status = it.child("statusDonasi").value.toString()
            if(status == "Success"){
                startActivity(Intent(this, DonasiBerhasil::class.java).apply {
                    putExtra("tanggalDonasi", user.tanggalDonasi)
                    putExtra("jenisDonasi", user.dropOffPickUp)
                    putExtra("idDonasi", user.idDonasi)
                    putExtra("statusDonasi", user.statusDonasi)
                    putExtra("judulDonasi", user.judulDonasi)
                    putExtra("alamatDonasi", user.alamatDonasi)
                })
                finish()
            }
        }
    }
}