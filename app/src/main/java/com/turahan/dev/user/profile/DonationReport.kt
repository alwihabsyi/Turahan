package com.turahan.dev.user.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.R
import com.turahan.dev.databinding.ActivityDonationReportBinding

class DonationReport : AppCompatActivity() {

    private lateinit var binding: ActivityDonationReportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("User")

        database.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            if(it.exists()){
                val uname = it.child("uname").value.toString()
                val tanggalBergabung = it.child("tanggalBergabung").value.toString()
                val totalPoin = it.child("totalPoin").value.toString()
                val poinSaatIni = it.child("poin").value.toString()
                val kaliDonasi = it.child("kaliDonasi").value.toString()

                binding.tvUname.text = uname
                binding.tvTglGabung.text = "Joined Since ${tanggalBergabung}"
                binding.tvTotalDonasi.text = totalPoin
                binding.tvPoinSaatIni.text = poinSaatIni
                binding.tvKaliDonasi.text = kaliDonasi
            }
        }
    }
}