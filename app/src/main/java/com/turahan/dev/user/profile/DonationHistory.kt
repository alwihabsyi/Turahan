package com.turahan.dev.user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.turahan.dev.DonationHistoryAdapter
import com.turahan.dev.data.DataDonasiMakanan
import com.turahan.dev.databinding.ActivityDonationHistoryBinding
import com.turahan.dev.user.MainActivity

class DonationHistory : AppCompatActivity() {

    private lateinit var binding: ActivityDonationHistoryBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<DataDonasiMakanan>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        userRecyclerView = binding.recyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<DataDonasiMakanan>()
        getItemsData()
    }

    private fun getItemsData() {
        database = FirebaseDatabase.getInstance().getReference("DonasiMakanan")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val items = itemSnapshot.getValue(DataDonasiMakanan::class.java)
                        if (auth.currentUser?.uid == items?.idUser)
                            userArrayList.add(items!!)
                    }
                    if(userArrayList.isEmpty()){
                        binding.tvNoTransaction.text = "Tidak Ada Transaksi"
                    }else{
                        binding.tvNoTransaction.text = null
                    }
                    userRecyclerView.adapter = DonationHistoryAdapter(userArrayList) {
                        Intent(baseContext, MainActivity::class.java).apply {
                            putExtra("user", it)
                            startActivity(this)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}