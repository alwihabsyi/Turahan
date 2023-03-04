package com.turahan.dev.user.home

import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.graphics.createBitmap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.InstruksiPembayaran
import com.turahan.dev.R
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.databinding.FragmentIsiDonasiBinding
import java.text.SimpleDateFormat
import java.util.*

class IsiDonasi : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentIsiDonasiBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var nominal: String? = null
    private var metodeBayar: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentIsiDonasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")

        binding.cardBca.setOnClickListener {
            metodeBayar = "BCA"
            binding.cardBca.strokeColor = Color.RED
            binding.cardBri.strokeColor = Color.BLACK
            binding.cardBni.strokeColor = Color.BLACK
        }

        binding.cardBri.setOnClickListener {
            metodeBayar = "BRI"
            binding.cardBca.strokeColor = Color.BLACK
            binding.cardBri.strokeColor = Color.RED
            binding.cardBni.strokeColor = Color.BLACK
        }

        binding.cardBni.setOnClickListener {
            metodeBayar = "BNI"
            binding.cardBca.strokeColor = Color.BLACK
            binding.cardBri.strokeColor = Color.BLACK
            binding.cardBni.strokeColor = Color.RED
        }

        binding.btnBayar.setOnClickListener {
            nominal = binding.inputNominal.text.toString()
            if (binding.inputNominal.text!!.isEmpty()) {
                Toast.makeText(
                    context?.applicationContext,
                    "Please Input Donation Amount!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if (nominal!!.toInt() < 10000) {
                Toast.makeText(
                    context?.applicationContext,
                    "Minimum Donation Amount is IDR 10.000",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("dd MMMM YYYY | HH:mm")
            var idDonasi = "${auth.currentUser?.displayName}${getRandomString(5)}"
            val resources = context?.resources
//            val uriFile = Uri.parse(
//                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources!!.getResourcePackageName(
//                    R.drawable.cash_ic
//                ) + "/" + resources.getResourceTypeName(R.drawable.cash_ic) + "/" + resources.getResourceEntryName(
//                    R.drawable.cash_ic
//                )
//            )

            val dataDonasi = DataDonasi(
                auth.currentUser!!.uid,
                idDonasi,
                "${auth.currentUser!!.displayName}'s Cash Donation",
                "Cash",
                tanggalDonasi,
                nominal,
                "Pending",
                "",
                "Cash: ${metodeBayar} Payment"
                )

            databaseDonasi.child(idDonasi).get().addOnSuccessListener{
                if(it.exists()){
                    val idDonasiuser = it.child("idDonasi").value.toString()
                    if (idDonasiuser == idDonasi) {
                        idDonasi = "${auth.currentUser?.displayName}+${getRandomString(5)}"
                    }
                }

                databaseDonasi.child(idDonasi).setValue(dataDonasi).addOnSuccessListener {
                    startActivity(Intent(requireContext(), InstruksiPembayaran::class.java).apply {
                        putExtra("idDonasi", idDonasi)
                        putExtra("tanggalDonasi", tanggalDonasi)
                        putExtra("paymentMethod", metodeBayar)
                        putExtra("nominal", nominal)
                    })

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
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

}