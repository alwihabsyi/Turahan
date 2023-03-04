package com.turahan.dev

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.turahan.dev.data.DataDonasi
import com.turahan.dev.databinding.ActivityPickLocationBinding
import java.util.*

class PickLocation : AppCompatActivity(),OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMarkerDragListener {

    private lateinit var binding: ActivityPickLocationBinding
    private lateinit var geoCoder: Geocoder
    private lateinit var addresses: List<Address>
    private lateinit var tvDetailAddress: TextView
    private var mGoogleMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    var mCurrentLocation: Marker? = null
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private lateinit var loader: ProgressBar
    private val permissionCode = 101

    private lateinit var databaseUser: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    var currentFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        databaseUser = FirebaseDatabase.getInstance().getReference("Donasi")
        currentFile = Uri.EMPTY

        tvDetailAddress = findViewById(R.id.tv_detail_alamat)
        loader = findViewById(R.id.progress_bar)
        supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMaps) as SupportMapFragment

        client = LocationServices.getFusedLocationProviderClient(this)

        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
        } else {
            tvDetailAddress.text = ""
            loader.visibility = View.VISIBLE
            getCurrentLocation()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, PickUp::class.java))
        }

        //Intent Extra dari Activity Pickup
        val idUser = intent.getStringExtra("idUser")
        var idDonasi = intent.getStringExtra("idDonasi")
        val judulDonasi = intent.getStringExtra("judulDonasi")
        val tanggalDonasi = intent.getStringExtra("tanggalDonasi")
        val kategoriDonasi = intent.getStringExtra("kategoriDonasi")
        val statusDonasi = intent.getStringExtra("statusDonasi")
        binding.tvPerantara.text = intent.getStringExtra("fotoDonasi")
        currentFile = Uri.parse(binding.tvPerantara.text.toString())

        binding.btnDonateNow.setOnClickListener {
            val alamatDonasi = tvDetailAddress.text.toString()

            databaseUser.child("idDonasi").get().addOnSuccessListener {
                val idDonasiuser = it.child("idDonasi").value.toString()
                if (idDonasiuser == idDonasi) {
                    idDonasi = "${auth.currentUser?.displayName}+${getRandomString(5)}"
                }
                val donasiUser = DataDonasi(
                    idUser,
                    idDonasi,
                    judulDonasi,
                    alamatDonasi,
                    tanggalDonasi,
                    kategoriDonasi,
                    statusDonasi,
                    " ",
                    "Pick Up"
                )

                databaseUser.child(idDonasi!!).setValue(donasiUser).addOnSuccessListener {
                    uploadDonationImage()
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnAlamatLain.setOnClickListener {

            startActivity(Intent(this, CustomAdressActivity::class.java).apply {
                putExtra("idUser","${auth.currentUser?.uid}")
                putExtra("idDonasi",idDonasi)
                putExtra("judulDonasi",judulDonasi)
                putExtra("alamatDonasi"," ")
                putExtra("tanggalDonasi",tanggalDonasi)
                putExtra("kategoriDonasi",kategoriDonasi)
                putExtra("statusDonasi",statusDonasi)
                putExtra("fotoDonasi",currentFile.toString())
            })
        }

    }

    private fun uploadDonationImage() {
        currentFile = Uri.parse(binding.tvPerantara.text.toString())
        val id = intent.getStringExtra("idDonasi")
        storageRef = FirebaseStorage.getInstance().getReference("donationImages/$id")
        storageRef.putFile(currentFile!!).addOnSuccessListener {

            it.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->

                    databaseUser = FirebaseDatabase.getInstance().getReference("Donasi")
                    databaseUser.child(id!!).get().addOnSuccessListener { dataSnapshot ->
                        val idUser = dataSnapshot.child("idUser").value.toString()
                        val idDonasi = dataSnapshot.child("idDonasi").value.toString()
                        val judulDonasi = dataSnapshot.child("judulDonasi").value.toString()
                        val alamatDonasi = dataSnapshot.child("alamatDonasi").value.toString()
                        val kategoriDonasi = dataSnapshot.child("kategoriDonasi").value.toString()
                        val statusDonasi = dataSnapshot.child("statusDonasi").value.toString()
                        val tanggalDonasi = dataSnapshot.child("tanggalDonasi").value.toString()
                        val dropOffPickup = dataSnapshot.child("dropOffPickUp").value.toString()

                        val donasiUser = DataDonasi(
                            idUser,
                            idDonasi,
                            judulDonasi,
                            alamatDonasi,
                            tanggalDonasi,
                            kategoriDonasi,
                            statusDonasi,
                            "$uri",
                            dropOffPickup
                        )

                        databaseUser.child(id).setValue(donasiUser).addOnSuccessListener {
                            startActivity(Intent(this, OrderSuccess::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun changeLocationText() {
        // Get address from latitude
        geoCoder = Geocoder(this, Locale.getDefault())

        addresses = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1) as List<Address>
        tvDetailAddress.text = addresses[0].getAddressLine(0).toString()
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val task = client.lastLocation
        task.addOnSuccessListener {
            val location = it
            if (location != null) {
                currentLocation = location
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.fragmentMaps) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this)

                changeLocationText()
                if (tvDetailAddress.text != ""){
                    loader.visibility = View.GONE
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .draggable(true)
        googleMap?.isMyLocationEnabled = true
        googleMap?.setOnMyLocationButtonClickListener(this)
        googleMap?.setOnMyLocationClickListener(this)
        googleMap?.setOnMarkerDragListener(this)
        googleMap?.setOnMapLongClickListener(this)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mCurrentLocation = googleMap?.addMarker(markerOptions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0]==
                PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        mCurrentLocation?.remove()
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .draggable(true)
        mCurrentLocation = mGoogleMap?.addMarker(markerOptions)
        changeLocationText()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        mCurrentLocation?.remove()
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .draggable(true)
        mCurrentLocation = mGoogleMap?.addMarker(markerOptions)
        changeLocationText()
    }

    override fun onMarkerDragStart(marker: Marker?) {
        marker?.position
    }

    override fun onMarkerDrag(marker: Marker?) {
        marker?.position
    }

    override fun onMarkerDragEnd(marker: Marker?) {
        addresses =
            geoCoder.getFromLocation(marker?.position!!.latitude, marker.position.longitude, 1) as List<Address>
        tvDetailAddress.text = addresses[0].getAddressLine(0).toString()
    }

    override fun onMapLongClick(latLng: LatLng?) {
        addresses = geoCoder.getFromLocation(latLng!!.latitude, latLng.longitude, 1) as List<Address>
        tvDetailAddress.text = addresses[0].getAddressLine(0).toString()
        mCurrentLocation?.remove()
        val markerOptions = MarkerOptions()
            .position(latLng!!)
            .draggable(true)
        mCurrentLocation = mGoogleMap?.addMarker(markerOptions)

    }
}