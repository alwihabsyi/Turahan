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
import com.turahan.dev.databinding.ActivityPickUpBinding
import com.turahan.dev.user.MainActivity
import java.text.SimpleDateFormat
import java.util.*

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
            var kategoriDonasi = " "
            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("yyyy/MM/dd HH:mm")
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