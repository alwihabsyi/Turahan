package com.turahan.dev

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.data.DataDonasiMakanan
import com.turahan.dev.databinding.ActivityPickUpBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class PickUp : AppCompatActivity() {

    private lateinit var binding: ActivityPickUpBinding
    private lateinit var databaseUser: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private var link: String = ""
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseUser =
            FirebaseDatabase.getInstance().getReference("DonasiMakanan")

        binding.fotoMakanan.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

        binding.backButtonPickUp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.pickLocButton.setOnClickListener {
            val judulDonasi = binding.etJudulDonasi.text
            val radioButtonEat = binding.eatableRadioButton
            val radioButtonUneat = binding.uneatableRadioButton
            val foto = binding.fotoMakanan
            var kategoriDonasi = " "
            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("yyyy/MM/dd HH:mm:ss")
            var id = "${auth.currentUser?.displayName}${getRandomString(5)}"

            if (judulDonasi.isEmpty()) {
                Toast.makeText(this, "Harap Isi Semua Field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (radioButtonEat.isChecked) {
                kategoriDonasi = "Layak Makan"
            } else if (radioButtonUneat.isChecked) {
                kategoriDonasi = "Tidak layak makan"
            }

            databaseUser.child("idDonasi").get().addOnSuccessListener {
                val idDonasiuser = it.child("idDonasi").value.toString()
                if (idDonasiuser == id) {
                    id = "${auth.currentUser?.displayName}+${getRandomString(5)}"
                }
                val donasiUser = DataDonasiMakanan(
                    "${auth.currentUser?.uid}",
                    id,
                    judulDonasi.toString(),
                    "Alamat",
                    tanggalDonasi,
                    kategoriDonasi,
                    "Pending"
                )

                databaseUser.child(id).setValue(donasiUser).addOnSuccessListener {
                    Toast.makeText(this, "Sukses Upload", Toast.LENGTH_SHORT).show()
                    uploadDonationImage(id)
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

                    databaseUser = FirebaseDatabase.getInstance().getReference("DonasiMakanan")
                    databaseUser.child(id).get().addOnSuccessListener {
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

                        databaseUser.child(id).setValue(donasiUser).addOnSuccessListener {
                            Toast.makeText(this, "Sukses Upload", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                currentFile = it
                binding.fotoMakanan.setImageURI(it)
            }
        }
    }
}