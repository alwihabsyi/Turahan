package com.turahan.dev.user.profile

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.R
import com.turahan.dev.RetrofitInstance
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.data.TransactionService
import com.turahan.dev.data.TransactionStatus
import com.turahan.dev.databinding.ActivityDonasiProsesBinding
import com.turahan.dev.user.MainActivity
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DonasiProses() : AppCompatActivity() {

    private lateinit var binding: ActivityDonasiProsesBinding
    private lateinit var retService: TransactionService
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var orderId: String? = null
    private var alamat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonasiProsesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseUser = FirebaseDatabase.getInstance().getReference("User")
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")

        orderId = intent.getStringExtra("idDonasi")

//        databaseUser.child(auth.currentUser!!.uid).get().addOnSuccessListener {
//            alamat = it.child("alamat").value.toString()
//        }

        retService = RetrofitInstance
            .getRetrofitInstance()
            .create(TransactionService::class.java)

        getRequest()


        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.expandButton.setOnClickListener {
            if (binding.containerDetalJumlahDonasi.visibility == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                }
                binding.containerDetalJumlahDonasi.visibility = View.GONE
                binding.expandButton.setImageResource(R.drawable.expand_more)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.cvJumlahDonasi, AutoTransition())
                }
                binding.containerDetalJumlahDonasi.visibility = View.VISIBLE
                binding.expandButton.setImageResource(R.drawable.expand_less)
            }
        }
    }

    private fun getRequest() {
        val pathResponse: LiveData<Response<TransactionStatus>> = liveData {
            val response = retService.getTransaction(orderId!!)
            emit(response)
        }

        pathResponse.observe(this, Observer {
            val vaNumbers = it.body()?.vaNumbers?.listIterator()
            val grossAmount = it.body()?.gross_amount
            var metodeBayar: String? = null
            val transacTime = it.body()?.transaction_time
            val expiryTime = it.body()?.expiry_time
            var status = it.body()?.transaction_status
            val date = Calendar.getInstance().time
            val tanggalDonasi = date.toString("dd MMMM YYYY | HH:mm")

            if (vaNumbers != null) {
                while (vaNumbers.hasNext()) {
                    val item = vaNumbers.next()
                    binding.tvVaNumber.text = item.va_number
                    binding.tvMetodePembayaran.text = item.bank
                }

            }

            if(status == "settlement"){
                status = "Success"
            }else if(status == "pending"){
                status = "Pending"
            }

            binding.tvIdDonasi.text = orderId
            binding.tvJumlahDonasi.text = grossAmount
            binding.tvWaktuTransaksi.text = transacTime
            binding.tvTenggatWaktu.text = expiryTime
            binding.tvStatus.text = status
            metodeBayar = binding.tvMetodePembayaran.text.toString()

            if (metodeBayar == ""){
                databaseDonasi.child(orderId!!).removeValue()
                finish()
            }

            val dataDonasi =
                DataDonasi(
                    auth.currentUser!!.uid,
                    orderId,
                    "${auth.currentUser!!.displayName}'s Cash Donation",
                    "Cash",
                    tanggalDonasi,
                    grossAmount,
                    status,
                    "",
                    "Cash: $metodeBayar Payment"
                )

            databaseDonasi.child(orderId!!).setValue(dataDonasi).addOnFailureListener {
                Toast.makeText(this, "Gagal Input Data ke Database", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}