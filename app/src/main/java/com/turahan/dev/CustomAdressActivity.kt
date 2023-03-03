package com.turahan.dev

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.data.DataDonasiMakanan
import com.turahan.dev.data.DataUser
import com.turahan.dev.databinding.ActivityCustomAdressBinding

class CustomAdressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomAdressBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAdressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseUser = FirebaseDatabase.getInstance().getReference("User")
        databaseDonasi = FirebaseDatabase.getInstance().getReference("DonasiMakanan")

        binding.cbDefaultAddress.setOnClickListener {
            databaseUser.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                val alamat = it.child("alamat").value.toString()
                if (binding.cbDefaultAddress.isChecked) {
                    binding.etAlamat.setText(alamat)
                } else {
                    binding.etAlamat.text = null
                }
            }
        }

        val idUser = intent.getStringExtra("idUser")
        var idDonasi = intent.getStringExtra("idDonasi")
        val judulDonasi = intent.getStringExtra("judulDonasi")
        val tanggalDonasi = intent.getStringExtra("tanggalDonasi")
        val kategoriDonasi = intent.getStringExtra("kategoriDonasi")
        val statusDonasi = intent.getStringExtra("statusDonasi")
        currentFile = Uri.parse(intent.getStringExtra("fotoDonasi"))

        binding.btnDonateNow.setOnClickListener {
            val alamatDonasi = "${binding.etAlamat.text} , ${binding.etDetailAlamat.text} , ${binding.etJudulAlamat.text}"
            databaseDonasi.child("idDonasi").get().addOnSuccessListener {
                val idDonasiuser = it.child("idDonasi").value.toString()
                if (idDonasiuser == idDonasi) {
                    idDonasi = "${auth.currentUser?.displayName}+${getRandomString(5)}"
                }
                val donasiUser = DataDonasiMakanan(
                    idUser,
                    idDonasi,
                    judulDonasi,
                    alamatDonasi,
                    tanggalDonasi,
                    kategoriDonasi,
                    statusDonasi,
                    " "
                )

                databaseDonasi.child(idDonasi!!).setValue(donasiUser).addOnSuccessListener {
                    uploadDonationImage(idDonasi!!)
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadDonationImage(id: String) {
        storageRef = FirebaseStorage.getInstance().getReference("donationImages/" + id)
        storageRef.putFile(currentFile!!).addOnSuccessListener {

            it.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener {
                    val mapImage = it

                    databaseDonasi = FirebaseDatabase.getInstance().getReference("DonasiMakanan")
                    databaseDonasi.child(id).get().addOnSuccessListener {
                        val idUser = it.child("idUser").value.toString()
                        val idDonasi = it.child("idDonasi").value.toString()
                        val judulDonasi = it.child("judulDonasi").value.toString()
                        val alamatDonasi = it.child("alamatDonasi").value.toString()
                        val kategoriDonasi = it.child("kategoriDonasi").value.toString()
                        val statusDonasi = it.child("statusDonasi").value.toString()
                        val tanggalDonasi = it.child("tanggalDonasi").value.toString()

                        val donasiUser = DataDonasiMakanan(
                            idUser,
                            idDonasi,
                            judulDonasi,
                            alamatDonasi,
                            tanggalDonasi,
                            kategoriDonasi,
                            statusDonasi,
                            "${mapImage}"
                        )

                        databaseDonasi.child(id).setValue(donasiUser).addOnSuccessListener {
                            startActivity(Intent(this, OrderSuccess::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}