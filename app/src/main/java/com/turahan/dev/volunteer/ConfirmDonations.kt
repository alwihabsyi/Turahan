package com.turahan.dev.volunteer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.IResultReceiver.Default
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.CampaignDonateAdapter
import com.turahan.dev.R
import com.turahan.dev.RvCampaignAdapter
import com.turahan.dev.data.DataCampaign
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.databinding.ActivityConfirmDonationsBinding

class ConfirmDonations : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmDonationsBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<DataDonasi>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmDonationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        userArrayList = arrayListOf()
        val checkBox = findViewById<CheckBox>(R.id.checkboxCampaign)
        val judulCampaign = intent.getStringExtra("judulCampaign")
        val fotoBukti = intent.getStringExtra("fotoBukti")
        currentFile = Uri.parse(fotoBukti)

        userRecyclerView = binding.rvCampaign
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.itemAnimator = DefaultItemAnimator()
        userRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL
            )
        )
        userRecyclerView.setHasFixedSize(true)
        getItemsData()

        binding.btnConfDonation.setOnClickListener {
            for (i in userArrayList) {
                if (i.isSelected == true) {
                    storageRef =
                        FirebaseStorage.getInstance().getReference("donationProof/$judulCampaign")
                    storageRef.putFile(currentFile!!).addOnSuccessListener {

                        it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                                database = FirebaseDatabase.getInstance().getReference("Donasi")
                                database.child(i.idDonasi!!).get()
                                    .addOnSuccessListener { dataSnapshot ->
                                        if (dataSnapshot.exists()) {
                                            val idUser =
                                                dataSnapshot.child("idUser").value.toString()
                                            val idDonasi =
                                                dataSnapshot.child("idDonasi").value.toString()
                                            val judulDonasi =
                                                dataSnapshot.child("judulDonasi").value.toString()
                                            val fotoDonasi =
                                                dataSnapshot.child("fotoDonasi").value.toString()
                                            val alamatDonasi =
                                                dataSnapshot.child("alamatDonasi").value.toString()
                                            val kategoriDonasi =
                                                dataSnapshot.child("kategoriDonasi").value.toString()
                                            val statusDonasi =
                                                dataSnapshot.child("statusDonasi").value.toString()
                                            val tanggalDonasi =
                                                dataSnapshot.child("tanggalDonasi").value.toString()
                                            val dropOffPickup =
                                                dataSnapshot.child("dropOffPickUp").value.toString()

                                            val donasiUser = DataDonasi(
                                                idUser,
                                                idDonasi,
                                                judulDonasi,
                                                alamatDonasi,
                                                tanggalDonasi,
                                                kategoriDonasi,
                                                statusDonasi,
                                                fotoDonasi,
                                                dropOffPickup,
                                                judulCampaign,
                                                "$uri"
                                            )

                                            database.child(idDonasi).setValue(donasiUser).addOnFailureListener {
                                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                            Toast.makeText(this, "Add Campaign Report Success", Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }
        }
    }

    private fun getItemsData() {
        database = FirebaseDatabase.getInstance().getReference("Donasi")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val items = itemSnapshot.getValue(DataDonasi::class.java)
                        userArrayList.add(items!!)
                    }
                    if (userArrayList.isEmpty()) {
                        binding.tvNoTransaction.text = "Tidak Ada Transaksi"
                    } else {
                        binding.tvNoTransaction.text = null
                    }
                    userRecyclerView.adapter = CampaignDonateAdapter(userArrayList) {
                        it.isSelected = it.isSelected != true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}