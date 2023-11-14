package com.example.birdy

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.birdy.*
import com.example.birdy.accounts.LoginActivity
import com.example.birdy.accounts.UserDAO
import com.example.birdy.maps.MapHandler
import com.example.birdy.notebook.Notebook
import com.example.birdy.observations.ViewObservations
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    private lateinit var btnSaveObservation: Button
    private lateinit var btnCenterMap: ImageButton
    private lateinit var btnMenu: ImageButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    lateinit var currentLocation: Location

    private lateinit var sharedPref: SharedPreferences
    private lateinit var activity: MainActivity
    private lateinit var mapHandler: MapHandler
    private var isDarkTheme = true // Initial theme


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set the theme based on the initial condition
        setAppTheme(isDarkTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this
        sharedPref = getDefaultSharedPreferences(this)
        if (!sharedPref.contains(getString(R.string.saved_dist_key))) {
            with (sharedPref.edit()) {
                putInt(getString(R.string.saved_dist_key), 5)
                commit()
            }
        }
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)

        mapHandler = MapHandler(this, supportFragmentManager)
        startNavDrawer()
        subscribeToLocationUpdates()

        btnSaveObservation = findViewById(R.id.btnSaveObservation)
        btnSaveObservation.setOnClickListener {
            val intent = Intent(this, ViewObservations::class.java)
            startActivity(intent)
        }

        btnCenterMap = findViewById(R.id.btnMainCenter)
        btnCenterMap.setOnClickListener {
            mapHandler.centerOnUser()
        }

        btnMenu = findViewById(R.id.btnMainMenu)
        btnMenu.setOnClickListener {
            showOptionsDialog()
        }
    }

    private fun showOptionsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.map_settings_dialog)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()

        val etHotspotDists = dialog.findViewById<EditText>(R.id.etHotspotDistances)
        val cbShowHotspot = dialog.findViewById<CheckBox>(R.id.cbShowHotspots)
        val cbShowObservations = dialog.findViewById<CheckBox>(R.id.cbShowObservations)
        val btnClose = dialog.findViewById<Button>(R.id.btnCloseDialog)
        val btnSave = dialog.findViewById<Button>(R.id.btnSaveDialog)

        etHotspotDists.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { return }
            override fun afterTextChanged(p0: Editable?) { return }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    etHotspotDists.text = Editable.Factory.getInstance().newEditable("0")
                    return
                }
                val dist = s.toString().toInt()
                if (dist < 0) {
                    etHotspotDists.text = Editable.Factory.getInstance().newEditable("0")
                    return
                }
                if (dist > 500) {
                    etHotspotDists.text = Editable.Factory.getInstance().newEditable("500")
                    return
                }
            }
        })

        btnSave.setOnClickListener {
            with (sharedPref.edit()) {
                putInt(getString(R.string.saved_dist_key), etHotspotDists.text.toString().toInt())
                commit()
            }
            mapHandler.clearMap()
            if (cbShowHotspot.isChecked) {
                mapHandler.loadHotspots()
            }
            if (cbShowObservations.isChecked) {
                mapHandler.loadObservations()
            }
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(width, -2)
    }
    private fun setAppTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            setTheme(R.style.Night)
        } else {
            setTheme(R.style.Light)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("Main", item.toString())
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        }
    }

    private fun startNavDrawer() {
        // https://www.geeksforgeeks.org/navigation-drawer-in-android/
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_map ->
                    Log.d("Navigation", "Map")
                R.id.nav_sightings -> {
                    val intent = Intent(this, ViewObservations::class.java)
                    startActivity(intent)
                }
                R.id.nav_notebook -> {
                    val intent = Intent(this, Notebook::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    val userDao = UserDAO(this)
                    userDao.logout()
                    startActivity(intent)
                }
                R.id.theme_switch -> {

                    isDarkTheme = !isDarkTheme;
                    recreate();

                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationUpdates() {
        // https://www.geeksforgeeks.org/how-to-get-current-location-in-android/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(1)).build()
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let {
                    currentLocation = it
                    setLocation(currentLocation)
                } ?: {
                    Log.d(TAG, "Location information isn't available.")
                }
            }
        }
        if (isLocationPermissionGranted()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
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
            mapHandler.setUserPosition(LatLng(location.latitude, location.longitude))
            with (sharedPref.edit()) {
                putString(getString(R.string.saved_lat_key), location.latitude.toString())
                putString(getString(R.string.saved_lng_key), location.longitude.toString())
                commit()
            }
        }
    }



}