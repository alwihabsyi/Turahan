package com.turahan.dev

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.data.Constants
import com.turahan.dev.databinding.ActivityPickUpBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class PickUp : AppCompatActivity() {

    private lateinit var binding: ActivityPickUpBinding
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseDonasi =
            FirebaseDatabase.getInstance().getReference("Donasi")

        binding.fotoMakanan.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

        binding.backButtonPickUp.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.pickLocButton.setOnClickListener {
            val judulDonasi = binding.etJudulDonasi.text
            val radioButtonEat = binding.eatableRadioButton
            val radioButtonUneat = binding.uneatableRadioButton
            var kategoriDonasi = " "
            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("dd MMMM YYYY | HH:mm")
            val id = "${auth.currentUser?.displayName}${getRandomString(5)}"

            if (judulDonasi.isEmpty()) {
                Toast.makeText(this, "Harap Isi Semua Field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (radioButtonEat.isChecked) {
                kategoriDonasi = "Layak Makan"
            } else if (radioButtonUneat.isChecked) {
                kategoriDonasi = "Tidak layak makan"
            }

            startActivity(Intent(this, PickLocation::class.java).apply {
                putExtra("idUser","${auth.currentUser?.uid}")
                putExtra("idDonasi",id)
                putExtra("judulDonasi",judulDonasi.toString())
                putExtra("alamatDonasi"," ")
                putExtra("tanggalDonasi",tanggalDonasi)
                putExtra("kategoriDonasi",kategoriDonasi)
                putExtra("statusDonasi","Pending")
                putExtra("fotoDonasi",currentFile.toString())
            })
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        capturePhoto()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, Constants.REQUEST_CODE_IMAGE_PICK)
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