package com.turahan.dev.user.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.turahan.dev.data.Constants
import com.turahan.dev.data.DataUser
import com.turahan.dev.databinding.ActivityChangeProfileBinding

class ChangeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProfileBinding
    private lateinit var storageRef: StorageReference
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("User")
        profnamepro()

        binding.profup.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

        binding.btnUpdate.setOnClickListener {
            val uname = binding.etUname.text
            val alamat = binding.etAlamat.text
            val uid = auth.currentUser?.uid

            if (uname.isNotEmpty()) {
                database.child(uid!!).get().addOnSuccessListener {
                    val poin = it.child("poin").value.toString()
                    val totalPoin = it.child("totalPoin").value.toString()
                    val kaliDonasi = it.child("kaliDonasi").value.toString()

                    val datauser = DataUser(uid, uname.toString(), alamat.toString(), totalPoin, poin, kaliDonasi)
                    database.child(uid).setValue(datauser).addOnSuccessListener {
                        uploadProfPic()
                    }
                }
            } else {
                Toast.makeText(this, "Harap Isi Username", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun profnamepro() {
        val currentUser = auth.currentUser
        if(currentUser != null){
            val profil = binding.profup
            val datalink = FirebaseDatabase.getInstance().getReference("userImages")
            datalink.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                if(it.exists()){
                    val url = it.child("url").value.toString()
                    Picasso
                        .get()
                        .load(url)
                        .into(profil)
                }
            }
        }
    }

    private fun uploadProfPic() {
        storageRef = FirebaseStorage.getInstance().getReference("User/" + auth.currentUser?.uid)
        storageRef.putFile(currentFile!!).addOnSuccessListener {

            it.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener {
                    val userId = auth.currentUser?.uid
                    val mapImage = mapOf(
                        "url" to it.toString()
                    )
                    val datalink = FirebaseDatabase.getInstance().getReference("userImages")
                    datalink.child(userId!!).setValue(mapImage)
                }

            Toast.makeText(this, "Berhasil Update Profil", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                currentFile = it
                binding.profup.setImageURI(it)
            }
        }
    }
}