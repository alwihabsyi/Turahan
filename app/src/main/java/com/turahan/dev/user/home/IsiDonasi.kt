package com.turahan.dev.user.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.turahan.dev.InstruksiPembayaran
import com.turahan.dev.R
import com.turahan.dev.databinding.FragmentIsiDonasiBinding

class IsiDonasi : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentIsiDonasiBinding
    private var nominal: String? = null
    private var metodeBayar: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentIsiDonasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardBca.setOnClickListener {
            metodeBayar = "BCA"
        }

        binding.cardBri.setOnClickListener {
            metodeBayar = "BRI"
        }

        binding.cardBni.setOnClickListener {
            metodeBayar = "BNI"
        }

        binding.btnBayar.setOnClickListener {
            nominal = binding.inputNominal.text.toString()

            if (nominal == "") {
                Toast.makeText(
                    context?.applicationContext,
                    "Silahkan input nominal donasi!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (nominal!!.toInt() >= 10000) {
                    val intent = Intent(context?.applicationContext, InstruksiPembayaran::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        context?.applicationContext,
                        "Minimal pembayaran Rp 10.000 ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}