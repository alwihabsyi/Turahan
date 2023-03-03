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
import com.turahan.dev.data.Constants
import com.turahan.dev.data.DataDonasiMakanan
import com.turahan.dev.databinding.ActivityDropOffBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class DropOff : AppCompatActivity() {

    private lateinit var binding: ActivityDropOffBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDropOffBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseDonasi =
            FirebaseDatabase.getInstance().getReference("DonasiMakanan")

        binding.ivImage2.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

        binding.backButtonDropOff.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.dropOffButton.setOnClickListener {
            val judulDonasi = binding.etJudulDonasi.text
            val radioButtonEat = binding.eatableRadioButton
            val radioButtonUneat = binding.uneatableRadioButton
            var kategoriDonasi = " "
            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("dd MMMM YYYY | HH:mm")
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

            databaseDonasi.child("idDonasi").get().addOnSuccessListener {
                val idDonasiuser = it.child("idDonasi").value.toString()
                if (idDonasiuser == id) {
                    id = "${auth.currentUser?.displayName}+${getRandomString(5)}"
                }
                val donasiUser = DataDonasiMakanan(
                    auth.currentUser!!.uid,
                    id,
                    judulDonasi.toString(),
                    binding.textView7.text.toString(),
                    tanggalDonasi,
                    kategoriDonasi,
                    "Pending",
                    " ",
                    "Drop Off"
                )

                databaseDonasi.child(id).setValue(donasiUser).addOnSuccessListener {
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

                    databaseDonasi = FirebaseDatabase.getInstance().getReference("DonasiMakanan")
                    databaseDonasi.child(id).get().addOnSuccessListener {
                        val idUser = it.child("idUser").value.toString()
                        val idDonasi = it.child("idDonasi").value.toString()
                        val judulDonasi = it.child("judulDonasi").value.toString()
                        val alamatDonasi = it.child("alamatDonasi").value.toString()
                        val kategoriDonasi = it.child("kategoriDonasi").value.toString()
                        val statusDonasi = it.child("statusDonasi").value.toString()
                        val tanggalDonasi = it.child("tanggalDonasi").value.toString()
                        val dropOffPickup = it.child("dropOffPickUp").value.toString()

                        val donasiUser = DataDonasiMakanan(
                            idUser,
                            idDonasi,
                            judulDonasi,
                            alamatDonasi,
                            tanggalDonasi,
                            kategoriDonasi,
                            statusDonasi,
                            "${mapImage}",
                            dropOffPickup
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


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
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
                binding.ivImage2.setImageURI(it)
            }
        }
    }
}
