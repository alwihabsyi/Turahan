package com.turahan.dev.volunteer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.databinding.FragmentVolProcessedOrderBinding
import com.turahan.dev.user.profile.DonationHistoryAdapter

class VolProcessedOrder : Fragment() {

    private lateinit var binding: FragmentVolProcessedOrderBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<DataDonasi>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVolProcessedOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        userArrayList = arrayListOf()

        userRecyclerView = binding.rvProcessedOrders
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.setHasFixedSize(true)
        getItemsData()

        binding.SwipeRefreshLayout.setOnRefreshListener {
            refresh()
            binding.SwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refresh() {
        userArrayList.clear()
        getItemsData()
    }

    private fun getItemsData() {
        database = FirebaseDatabase.getInstance().getReference("Donasi")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val items = itemSnapshot.getValue(DataDonasi::class.java)
                        if (items?.statusDonasi == "Processed")
                            userArrayList.add(items)
                    }
                    if(userArrayList.isEmpty()){
                        binding.tvNoTransaction.text = "Tidak Ada Transaksi"
                    }else{
                        binding.tvNoTransaction.text = null
                    }
                    userRecyclerView.adapter = DonationHistoryAdapter(userArrayList) {
                        Intent(requireContext(), VolDonationDetail::class.java).apply {
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

    override fun onResume() {
        super.onResume()
        refresh()
    }

}