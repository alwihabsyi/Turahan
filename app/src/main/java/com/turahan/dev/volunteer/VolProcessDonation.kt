package com.turahan.dev.volunteer

import android.app.Dialog
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
import com.turahan.dev.databinding.ActivityVolProcessDonationBinding

class VolProcessDonation : AppCompatActivity() {

    private lateinit var binding: ActivityVolProcessDonationBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolProcessDonationBinding.inflate(layoutInflater)
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

        var idDonasi = intent.getStringExtra("idDonasi")
        val judulDonasi = intent.getStringExtra("judulDonasi")
        val tanggalDonasi = intent.getStringExtra("tanggalDonasi")
        val jenisDonasi = intent.getStringExtra("jenisDonasi")
        val alamatDonasi = intent.getStringExtra("alamatDonasi")
        val kategoriDonasi = intent.getStringExtra("kategoriDonasi")
        val fotoDonasi = intent.getStringExtra("fotoDonasi")
        //Move Data
        binding.tvJenisDonasi.text = jenisDonasi
        binding.tvIdDonasi.text = idDonasi
        binding.tvStatus.text = "Success"
        binding.tvJudulDonasi.text = judulDonasi
        binding.tvAlamatDonasi.text = alamatDonasi
        binding.tvKategoriDonasi.text = kategoriDonasi

        Picasso
            .get()
            .load(fotoDonasi)
            .into(binding.ivFotoDonasi)

        if (jenisDonasi == "Pick Up") {
            binding.tvStatus.text = "Food is ready to be picked up"
        } else if (jenisDonasi == "Drop Off") {
            binding.tvStatus.text = "Donater is about to drop off the food"
        }

        binding.btnConfirm.setOnClickListener {
            databaseDonasi.child(idDonasi!!).get().addOnSuccessListener {
                val kategoriDonasi = it.child("kategoriDonasi").value.toString()
                val userUID = it.child("idUser").value.toString()
                val dropOffPickUp = it.child("dropOffPickUp").value.toString()

                val dataDonasiDone =
                    DataDonasi(
                        userUID,
                        idDonasi,
                        judulDonasi,
                        alamatDonasi,
                        tanggalDonasi,
                        kategoriDonasi,
                        "Success",
                        fotoDonasi,
                        dropOffPickUp
                    )

                databaseDonasi.child(idDonasi).setValue(dataDonasiDone).also {
                    databaseUser.child(userUID).get().addOnSuccessListener {
                        val uname = it.child("uname").value.toString()
                        val alamat = it.child("alamat").value.toString()
                        val poin = Integer.parseInt(it.child("poin").value.toString())
                        val totalpoin = Integer.parseInt(it.child("totalPoin").value.toString())
                        val kaliDonasi = Integer.parseInt(it.child("kaliDonasi").value.toString())
                        val tanggalBergabung = it.child("tanggalBergabung").value.toString()

                        val totalPoinFin = (totalpoin).plus(500).toString()
                        val poinFin = (poin).plus(500).toString()
                        val kaliDonasiFin = (kaliDonasi).plus(1).toString()


                        val userdata = DataUser(
                            userUID,
                            uname,
                            alamat,
                            totalPoinFin,
                            poinFin,
                            kaliDonasiFin,
                            tanggalBergabung
                        )
                        databaseUser.child(userUID).setValue(userdata)
                            .addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
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
                    databaseDonasi.child(idDonasi).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Donation Cancellation Success", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }

                nobtn.setOnClickListener {
                    dialog.cancel()
                }
            }
        }
    }
}