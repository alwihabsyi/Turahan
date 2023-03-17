package com.turahan.dev.user.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.turahan.dev.R
import com.turahan.dev.RetrofitInstance
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.data.TransactionService
import com.turahan.dev.data.TransactionStatus
import com.turahan.dev.databinding.ActivityDonationDetailBinding
import retrofit2.Response

class DonationDetail : AppCompatActivity() {

    private lateinit var binding: ActivityDonationDetailBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var retService: TransactionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("Donasi")

        binding.expandButton.setOnClickListener {
            if (binding.containerDetalJumlahDonasi.visibility == View.VISIBLE) {
                binding.containerDetalJumlahDonasi.visibility = View.GONE
                binding.expandButton.setImageResource(R.drawable.expand_more)
                TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
            } else {
                TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                binding.containerDetalJumlahDonasi.visibility = View.VISIBLE
                binding.expandButton.setImageResource(R.drawable.expand_less)
            }
        }

        val user = intent.getParcelableExtra<DataDonasi>("user")
        //Move Data
        binding.tvMetodePembayaran.text = user?.dropOffPickUp
        binding.tvIdDonasi.text = user?.idDonasi
        binding.tvJumlahDonasi.text = user?.judulDonasi
        binding.tvDetailKodeUnik.text = user?.kategoriDonasi
        binding.tvAlamatDonasi.text = user?.alamatDonasi

        if(user?.alamatDonasi == "Cash"){
            binding.textView20.text = getString(R.string.donation_amount)
        }

        if(user?.statusDonasi == "Processed"){
            if(user.dropOffPickUp == "Pick Up"){
                binding.tvMenungguPembayaran.text = getString(R.string.Disetujui)
                binding.tvStatusPlus.text = getString(R.string.waitPickUp)
                binding.tvStatus.text = getString(R.string.foodpickup)
            }else if(user.dropOffPickUp == "Drop Off"){
                binding.tvMenungguPembayaran.text = getString(R.string.Disetujui)
                binding.tvStatusPlus.text = getString(R.string.waitDropOff)
                binding.tvStatus.text = getString(R.string.donConfirmed)
            }
        }else{
            binding.tvStatus.text = user?.statusDonasi
        }

        if (user?.fotoDonasi != "") {
            Picasso
                .get()
                .load(user?.fotoDonasi)
                .into(binding.ivFotoDonasi)
        } else {
            binding.ivFotoDonasi.setImageResource(R.drawable.cash_ic)
        }

        //Btn Update
        binding.perbaruiStatusButton.setOnClickListener {
            database.child(user?.idDonasi!!).get().addOnSuccessListener {
                val status = it.child("statusDonasi").value.toString()
                if(status == "Pending"){
                    Toast.makeText(this, "Your donation is still waiting for approval", Toast.LENGTH_SHORT).show()
                }else if(status == "Success"){
                    startActivity(Intent(this, DonasiBerhasil::class.java).apply {
                        putExtra("tanggalDonasi", user.tanggalDonasi)
                        putExtra("jenisDonasi", user.dropOffPickUp)
                        putExtra("idDonasi", user.idDonasi)
                        putExtra("statusDonasi", user.statusDonasi)
                        putExtra("judulDonasi", user.judulDonasi)
                        putExtra("alamatDonasi", user.alamatDonasi)
                        putExtra("kategoriDonasi", user.kategoriDonasi)
                    })
                    finish()
                }else if(status == "Processed"){
                    if(user.dropOffPickUp == "Pick Up"){
                        Toast.makeText(this, "Donation Approved, your donation is getting picked up", Toast.LENGTH_SHORT).show()
                    }else if(user.dropOffPickUp == "Drop Off"){
                        Toast.makeText(this, "Donation Approved, you can drop off your donation at our address", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Btn Cancel
        binding.btnCancel.setOnClickListener {
            val dialogBinding = layoutInflater.inflate(R.layout.cancel_dialog, null)
            val dialog = Dialog(this)
            dialog.setContentView(dialogBinding)

            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val yesbtn = dialogBinding.findViewById<Button>(R.id.btn_yes)
            val nobtn = dialogBinding.findViewById<Button>(R.id.btn_no)

            yesbtn.setOnClickListener {
                database.child(user?.idDonasi!!).removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Donation Cancellation Success", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            nobtn.setOnClickListener {
                dialog.cancel()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        database = FirebaseDatabase.getInstance().getReference("Donasi")
        val user = intent.getParcelableExtra<DataDonasi>("user")
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
                    putExtra("kategoriDonasi", user.kategoriDonasi)
                    putExtra("judulCampaign", user.titleCampaign)
                    putExtra("buktiFoto", user.fotoBukti)
                })
                finish()
            }
        }

        if(user.alamatDonasi == "Cash" && user.statusDonasi == "Pending"){

            retService = RetrofitInstance
                .getRetrofitInstance()
                .create(TransactionService::class.java)

            val pathResponse: LiveData<Response<TransactionStatus>> = liveData {
                val response = retService.getTransaction(user.idDonasi)
                emit(response)
            }

            pathResponse.observe(this, Observer{
                val status = it.body()?.transaction_status
                if(status == "settlement"){
                    val dataDonasi =
                        DataDonasi(
                            auth.currentUser!!.uid,
                            user.idDonasi,
                            user.judulDonasi,
                            user.alamatDonasi,
                            user.tanggalDonasi,
                            user.kategoriDonasi,
                            "Success",
                            user.fotoDonasi,
                            user.dropOffPickUp
                        )
                    database.child(user.idDonasi).setValue(dataDonasi).addOnSuccessListener {
                        startActivity(Intent(this, DonasiBerhasil::class.java).apply {
                            putExtra("tanggalDonasi", user.tanggalDonasi)
                            putExtra("jenisDonasi", user.dropOffPickUp)
                            putExtra("idDonasi", user.idDonasi)
                            putExtra("statusDonasi", user.statusDonasi)
                            putExtra("judulDonasi", user.judulDonasi)
                            putExtra("alamatDonasi", user.alamatDonasi)
                            putExtra("kategoriDonasi", user.kategoriDonasi)
                        })
                        finish()
                    }
                }
            })

        }

    }
}