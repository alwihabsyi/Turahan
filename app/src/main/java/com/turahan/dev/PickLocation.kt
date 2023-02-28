package com.turahan.dev

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import java.util.*

class PickLocation : AppCompatActivity(),OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMarkerDragListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val btnAlamatLain = findViewById<CardView>(R.id.btn_alamat_lain)
        btnAlamatLain.setOnClickListener {
            CustomAddressFragment().show(supportFragmentManager, "sign up fragment")
        }

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
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude, Toast.LENGTH_SHORT).show()
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