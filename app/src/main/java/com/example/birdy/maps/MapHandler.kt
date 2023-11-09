package com.example.birdy.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.birdy.R
import com.example.birdy.observations.Sighting
import com.example.birdy.observations.SightingDAO
import com.example.birdy.utility.buildURLForHotspot
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.text.DateFormat
import kotlin.concurrent.thread

class MapHandler(activity: Activity, supportFragmentManager: FragmentManager): OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private var userMarker: Marker? = null
    private var mapFragment: SupportMapFragment

    private var fusedLocationClient: FusedLocationProviderClient
    private var fragmentManager: FragmentManager
    private var sharedPrefs: SharedPreferences
    private var activity: Activity

    private var latKey: String
    private var lngKey: String
    private var dstKey: String

    init {
        this.activity = activity
        sharedPrefs = getDefaultSharedPreferences(activity)
        latKey = activity.getString(R.string.saved_lat_key)
        lngKey = activity.getString(R.string.saved_lng_key)
        dstKey = activity.getString(R.string.saved_dist_key)
        fragmentManager = supportFragmentManager
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    fun loadHotspots() {
        thread {
            val lat = sharedPrefs.getString(latKey, "0")
            val lng = sharedPrefs.getString(lngKey, "0")
            val dist = sharedPrefs.getInt(dstKey, 0)
            val hotspotJson = try {
                if (lat != null && lng != null) {
                    buildURLForHotspot(lat, lng, dist)?.readText()
                } else {
                    Log.d("HOTSPOT", "Hotspot parameters invalid!")
                    return@thread
                }
            } catch (e: java.lang.Exception) {
                Log.d("HOTSPOT", "$e")
                return@thread
            }
            activity.runOnUiThread { readHotspotJson(hotspotJson) }
        }
    }

    private fun readHotspotJson(hotspotJson: String?) {
        val gson = Gson()
        val hotspotData: Array<Hotspot> = gson.fromJson(hotspotJson, Array<Hotspot>::class.java)
        createHotspotMarkers(hotspotData)
    }

    private fun createHotspotMarkers(hotspots: Array<Hotspot>) {
        for (hotspot in hotspots) {
            val lat = hotspot.lat
            val lng = hotspot.lng
            if (lat != null && lng != null) {
                val pos = LatLng(lat, lng)
                map.addMarker(MarkerOptions()
                    .position(pos)
                    .title(hotspot.locName)
                    .snippet("Lat: $lat | Lng: $lng")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.hotspot_icon))
                )
            }
        }
    }

    fun loadObservations() {
        thread {
            val sightingDAO = SightingDAO(activity)
            val user = sharedPrefs.getString(activity.getString(R.string.saved_username_key), "") ?: ""
            val sightings = sightingDAO.getSightings(user)
            activity.runOnUiThread { createObservationMarkers(sightings) }
        }
    }

    private fun createObservationMarkers(sightings: List<Sighting>) {
        val df = DateFormat.getDateInstance()
        for (s in sightings) {
            map.addMarker(MarkerOptions()
                .position(LatLng(s.lat, s.lng))
                .title(s.species)
                .snippet(df.format(s.date))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.observation_icon))
            )
        }
    }

    fun setUserPosition(position: LatLng) {
        if (userMarker != null) {
            userMarker!!.position = position
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        loadHotspots()
        addUserMarker()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        //map.clear()
        MapRouter.showDirection(
            map,
            LatLng(
                sharedPrefs.getString(latKey, "0")!!.toDouble(),
                sharedPrefs.getString(lngKey, "0")!!.toDouble()
            ),
            marker.position
        )
        return false
    }

    fun clearMap() {
        map.clear()
        addUserMarker()
    }

    @SuppressLint("MissingPermission")
    fun addUserMarker() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            userMarker = map.addMarker(MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 10F))
        }
    }
}