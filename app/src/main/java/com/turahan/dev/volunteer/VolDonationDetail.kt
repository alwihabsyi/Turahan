package com.turahan.dev.volunteer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.data.DataUser
import com.turahan.dev.databinding.ActivityVolDonationDetailBinding

class VolDonationDetail : AppCompatActivity() {

    private lateinit var binding: ActivityVolDonationDetailBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolDonationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")
        databaseUser = FirebaseDatabase.getInstance().getReference("User")

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
        binding.tvStatus.text = user?.statusDonasi
        binding.tvJumlahDonasi.text = user?.judulDonasi
        binding.tvDetailKodeUnik.text = user?.kategoriDonasi
        binding.tvalamat.text = user?.alamatDonasi


        if (user?.fotoDonasi != "") {
            Picasso
                .get()
                .load(user?.fotoDonasi)
                .into(binding.ivFotoDonasi)
        } else {
            binding.ivFotoDonasi.setImageResource(R.drawable.cash_ic)
        }

        //Btn Confirm
        binding.btnConfirm.setOnClickListener {
            var statusDonasi: String
            statusDonasi = if(user?.dropOffPickUp == "Pick Up" || user?.dropOffPickUp == "Drop Off")
                "Processed"
            else
                "Success"

            databaseDonasi.child(user?.idDonasi!!).get().addOnSuccessListener {
                val dataDonasiDone =
                    DataDonasi(
                        user.idUser,
                        user.idDonasi,
                        user.judulDonasi,
                        user.alamatDonasi,
                        user.tanggalDonasi,
                        user.kategoriDonasi,
                        statusDonasi,
                        user.fotoDonasi,
                        user.dropOffPickUp
                    )

                databaseDonasi.child(user.idDonasi).setValue(dataDonasiDone).also {
                    databaseUser.child(user.idUser!!).get().addOnSuccessListener {
                        if(statusDonasi == "Success"){
                            val uname = it.child("uname").value.toString()
                            val alamat = it.child("alamat").value.toString()
                            val poin = Integer.parseInt(it.child("poin").value.toString())
                            val totalpoin = Integer.parseInt(it.child("totalPoin").value.toString())
                            val kaliDonasi = Integer.parseInt(it.child("kaliDonasi").value.toString())

                            val totalPoinFin = (totalpoin).plus(500).toString()
                            val poinFin = (poin).plus(500).toString()
                            val kaliDonasiFin = (kaliDonasi).plus(1).toString()


                            val userdata = DataUser(user.idUser, uname, alamat, totalPoinFin, poinFin, kaliDonasiFin)
                            databaseUser.child(user.idUser).setValue(userdata).addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnSuccessListener {
                    Toast.makeText(this, "Confirm Donation Success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

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
                databaseDonasi.child(user?.idDonasi!!).removeValue().addOnSuccessListener {
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
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")
        val user = intent.getParcelableExtra<DataDonasi>("user")
        databaseDonasi.child(user?.idDonasi!!).get().addOnSuccessListener {
            val status = it.child("statusDonasi").value.toString()
            if(status == "Success"){
                startActivity(Intent(this, VolDonasiBerhasil::class.java).apply {
                    putExtra("tanggalDonasi", user.tanggalDonasi)
                    putExtra("jenisDonasi", user.dropOffPickUp)
                    putExtra("idDonasi", user.idDonasi)
                    putExtra("statusDonasi", user.statusDonasi)
                    putExtra("judulDonasi", user.judulDonasi)
                    putExtra("alamatDonasi", user.alamatDonasi)
                    putExtra("fotoDonasi", user.fotoDonasi)
                })
                finish()
            }else if(status == "Processed"){
                startActivity(Intent(this, VolProcessDonation::class.java).apply {
                    putExtra("tanggalDonasi", user.tanggalDonasi)
                    putExtra("jenisDonasi", user.dropOffPickUp)
                    putExtra("idDonasi", user.idDonasi)
                    putExtra("statusDonasi", user.statusDonasi)
                    putExtra("judulDonasi", user.judulDonasi)
                    putExtra("alamatDonasi", user.alamatDonasi)
                    putExtra("kategoriDonasi", user.kategoriDonasi)
                    putExtra("fotoDonasi", user.fotoDonasi)
                })
                finish()
            }
        }
    }
}