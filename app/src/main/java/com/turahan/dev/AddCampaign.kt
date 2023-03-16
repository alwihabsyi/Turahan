package com.turahan.dev

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.data.Constants
import com.turahan.dev.data.DataCampaign
import com.turahan.dev.databinding.ActivityAddCampaignBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddCampaign : AppCompatActivity() {

    private lateinit var binding: ActivityAddCampaignBinding
    private lateinit var databaseCampaign: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseCampaign = FirebaseDatabase.getInstance().getReference("Campaign")

        binding.btnBackAC.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Intent Camera Start
        binding.fotoCampaign.isEnabled = true
        if(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        } else{
            binding.fotoCampaign.isEnabled = true
        }

        binding.fotoCampaign.setOnClickListener {
            val takePic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePic, 101)
        }

        binding.btnSaveCampaign.setOnClickListener {
            val campaignTitle = binding.etCampaignTitle.text.toString()
            val campaignDescription = binding.etCampaignTitle.text.toString()
            val date = Calendar.getInstance().time
            val campaignDate = date.toString("dd MMMM YYYY | HH:mm")
            val idCampaign = "Campaign${System.currentTimeMillis()}"

            if(binding.etCampaignTitle.text!!.isEmpty() || binding.etCampaignDescription.text!!.isEmpty() || currentFile == null){
                Toast.makeText(this, "Make sure to fill all the fields and add campaign photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            storageRef = FirebaseStorage.getInstance().getReference("donationImages/$idCampaign")
            storageRef.putFile(currentFile!!).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {Uri ->

                    val dataCampaign = DataCampaign(idCampaign, campaignDate, campaignTitle, campaignDescription, "$Uri")
                    databaseCampaign.child(campaignTitle).setValue(dataCampaign).addOnSuccessListener {
                        Toast.makeText(this, "Add Campaign Succeed", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {Exception ->
                        Toast.makeText(this, Exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
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

        if(requestCode == 101) {
            val pic: Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            val bytes: ByteArrayOutputStream = ByteArrayOutputStream()
            if(pic!=null){
                pic.compress(Bitmap.CompressFormat.PNG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(this.contentResolver, pic, "Title", null)
                currentFile = Uri.parse(path)
                binding.fotoCampaign.setImageBitmap(pic)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                currentFile = it
                binding.fotoCampaign.setImageURI(it)
            }
        }
    }
}