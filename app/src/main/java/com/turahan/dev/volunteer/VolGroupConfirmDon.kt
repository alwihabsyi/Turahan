package com.turahan.dev.volunteer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.turahan.dev.R
import com.turahan.dev.RvCampaignAdapter
import com.turahan.dev.data.Constants
import com.turahan.dev.data.DataCampaign
import com.turahan.dev.databinding.ActivityVolGroupConfirmDonBinding
import java.io.ByteArrayOutputStream

class VolGroupConfirmDon : AppCompatActivity() {

    private lateinit var binding: ActivityVolGroupConfirmDonBinding
    private lateinit var arrayList: ArrayList<String>
    private lateinit var databaseCampaign: DatabaseReference
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolGroupConfirmDonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseCampaign = FirebaseDatabase.getInstance().getReference("Campaign")
        arrayList = ArrayList()

        val autoCompleteTextView = binding.acCampaignTitle
        getItemsData()
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, arrayList)
        autoCompleteTextView.setAdapter(adapter)

        binding.btnFotoCampaign.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, Constants.REQUEST_CODE_IMAGE_PICK)
            }
        }

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

        binding.btnNext.setOnClickListener {
            var idCampaign = ""

            if(autoCompleteTextView.text.toString().isEmpty()){
                Toast.makeText(this, "You Have To Pick Campaign", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, ConfirmDonations::class.java).apply {
                putExtra("judulCampaign",autoCompleteTextView.text.toString())
                putExtra("fotoBukti",currentFile.toString())
            })
        }

    }

    private fun getItemsData() {
        databaseCampaign = FirebaseDatabase.getInstance().getReference("Campaign")
        databaseCampaign.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val items = itemSnapshot.getValue(DataCampaign::class.java)
                        arrayList.add(items?.campaignTitle!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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