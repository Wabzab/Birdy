package com.example.birdy

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.MenuItem;
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.birdy.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    lateinit var currentLocation: Location

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.navigation_view)

        // https://www.geeksforgeeks.org/navigation-drawer-in-android/
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_map ->
                    Log.d("Navigation", "Map")
                R.id.nav_sightings ->
                    Log.d("Navigation", "Sightings")
                R.id.nav_settings ->
                    Log.d("Navigation", "Settings")
                R.id.nav_logout ->
                    Log.d("Navigation", "Logout")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // https://www.geeksforgeeks.org/how-to-get-current-location-in-android/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    currentLocation = it
                    setLocation(currentLocation)
                    Log.d(TAG, "Location updated")
                    // use latitude and longitude as per your need
                } ?: {
                    Log.d(TAG, "Location information isn't available.")
                }
            }
        }
        if (isLocationPermissionGranted()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("Main", item.toString())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Load anything onto the map when it is ready like applying map markers
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
            false
        } else {
            true
        }
    }


    private fun setLocation(location: Location?) {
        if (location != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
                10F
            ))
        }

    }

}