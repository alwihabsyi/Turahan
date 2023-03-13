package com.turahan.dev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.turahan.dev.data.Constants
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.internal.constant.Authentication
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.CreditCard
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.uikit.api.model.BankType
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.databinding.ActivityPaymentMidtransBinding
import com.turahan.dev.user.profile.DonasiBerhasil
import com.turahan.dev.user.profile.DonasiProses
import com.turahan.dev.user.profile.DonationDetail

class PaymentMidtrans : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentMidtransBinding
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseDonasi: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var firstName: String? = null
    private var lastName: String? = null
    private var phone: String? = null
    private var email: String? = null
    private var address: String? = null
    private var city: String? = null
    private var postalCode: String? = null
    private var convertDonasi: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMidtransBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseUser = FirebaseDatabase.getInstance().getReference("User")
        databaseDonasi = FirebaseDatabase.getInstance().getReference("Donasi")

        binding.btn10k.setOnClickListener { nominalButtonPress() }
        binding.btn25k.setOnClickListener { nominalButtonPress() }
        binding.btn50k.setOnClickListener { nominalButtonPress() }
        binding.btn100k.setOnClickListener { nominalButtonPress() }

        databaseUser.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            firstName = it.child("firstName").value.toString()
            lastName = it.child("lastName").value.toString()
            phone = it.child("nomorTelpon").value.toString()
            email = auth.currentUser!!.email
            address = it.child("alamat").value.toString()
            city = it.child("kota").value.toString()
            postalCode = it.child("kodePos").value.toString()
        }

        SdkUIFlowBuilder.init()
            .setClientKey(Constants.CLIENT_KEY)
            .setContext(applicationContext)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->
                // this is logic
            })
            .setMerchantBaseUrl(Constants.BASE_URL)
            .enableLog(true)
            .setLanguage("id")
            .buildSDK()

        binding.btnBayar.setOnClickListener {
            val donasi = binding.inputNominal.text
            val orderID = "Turahan" + System.currentTimeMillis().toShort() + ""

            if (donasi!!.isEmpty()) {
                Toast.makeText(
                    this,
                    "Silahkan input nominal donasi!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else if(Integer.parseInt(donasi.toString()) < 10000){
                Toast.makeText(
                    this,
                    "Minimal pembayaran Rp 10.000 ",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val dataDonasi = DataDonasi(idUser = auth.currentUser!!.uid, idDonasi = orderID, alamatDonasi = "Cash")
            databaseDonasi.child(orderID).setValue(dataDonasi).addOnSuccessListener {

                val convertDonasi = donasi.toString().toDouble()
                val transactionRequest = TransactionRequest(orderID, convertDonasi)
                val detail = ItemDetails("Turahan", convertDonasi, 1, "donasi")
                val itemDetails = ArrayList<ItemDetails>()
                itemDetails.add(detail)
                uiKitDetails(transactionRequest)
                transactionRequest.itemDetails = itemDetails
                MidtransSDK.getInstance().transactionRequest = transactionRequest
                startActivity(Intent(this, DonasiProses::class.java).putExtra("idDonasi", orderID))
                MidtransSDK.getInstance().startPaymentUiFlow(this)

            }.addOnFailureListener {
                Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun nominalButtonPress() {
        binding.btn10k.setOnClickListener {
            binding.inputNominal.setText("10000")
            binding.btn10k.strokeColor = resources.getColor(R.color.dark_red)
            binding.btn25k.strokeColor = resources.getColor(R.color.white)
            binding.btn50k.strokeColor = resources.getColor(R.color.white)
            binding.btn100k.strokeColor = resources.getColor(R.color.white)
        }

        binding.btn25k.setOnClickListener {
            binding.inputNominal.setText("25000")
            binding.btn10k.strokeColor = resources.getColor(R.color.white)
            binding.btn25k.strokeColor = resources.getColor(R.color.dark_red)
            binding.btn50k.strokeColor = resources.getColor(R.color.white)
            binding.btn100k.strokeColor = resources.getColor(R.color.white)
        }

        binding.btn50k.setOnClickListener {
            binding.inputNominal.setText("50000")
            binding.btn10k.strokeColor = resources.getColor(R.color.white)
            binding.btn25k.strokeColor = resources.getColor(R.color.white)
            binding.btn50k.strokeColor = resources.getColor(R.color.dark_red)
            binding.btn100k.strokeColor = resources.getColor(R.color.white)
        }

        binding.btn100k.setOnClickListener {
            binding.inputNominal.setText("100000")
            binding.btn10k.strokeColor = resources.getColor(R.color.white)
            binding.btn25k.strokeColor = resources.getColor(R.color.white)
            binding.btn50k.strokeColor = resources.getColor(R.color.white)
            binding.btn100k.strokeColor = resources.getColor(R.color.dark_red)
        }
    }

    fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = "$firstName $lastName"
        customerDetails.phone = phone
        customerDetails.firstName = firstName
        customerDetails.lastName = lastName
        customerDetails.email = email
        val shippingAddress = ShippingAddress()
        shippingAddress.address = address
        shippingAddress.city = city
        shippingAddress.postalCode = postalCode
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address  = address
        billingAddress.city = city
        billingAddress.postalCode = postalCode
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails

        val creditCardOptions = CreditCard()
        // Set to true if you want to save card to Snap
        creditCardOptions.isSaveCard = false
        // Set to Authentication 3DS to save card token as `one click` token
        creditCardOptions.authentication = Authentication.AUTH_3DS
        // Set bank name when using MIGS channel
        creditCardOptions.bank = BankType.BCA
        creditCardOptions.bank = BankType.BNI
        creditCardOptions.bank = BankType.BRI
        // Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
        creditCardOptions.channel = CreditCard.MIGS
        // Set Credit Card Options
        transactionRequest.creditCard = creditCardOptions
    }
}