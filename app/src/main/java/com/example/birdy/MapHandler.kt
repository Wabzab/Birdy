package com.example.birdy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlin.concurrent.thread

class MapHandler(activity: Activity, supportFragmentManager: FragmentManager): OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var map: GoogleMap
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

    fun showRouteToDest(destination: LatLng) {
        val lat = sharedPrefs.getString(latKey, "0")
        val lng = sharedPrefs.getString(lngKey, "0")
        val start = LatLng(lat!!.toDouble(), lng!!.toDouble())
        thread {
            val routeJson = try {
                buildURLForRoute(start, destination).readText()
            } catch (e: java.lang.Exception) {
                Log.d("ROUTES", "$e")
                return@thread
            }
            activity.runOnUiThread { digestRouteJson(routeJson) }
        }
    }

    private fun digestRouteJson(routeJson: String?) {
        val gson = Gson()
        val routeData: Route = gson.fromJson(routeJson, Route::class.java)
        Log.d("ROUTES", "${routeData.routes}")
    }

    private fun loadHotspots() {
        thread {
            Log.d("HOTSPOT", "Hotspot thread started.")
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
        Log.d("HOTSPOT", "Reading hotspot json.")
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
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        loadHotspots()
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 10F))
            showRouteToDest(LatLng(location.latitude, location.longitude))
            MapRouter.showDirection(
                map,
                LatLng(sharedPrefs.getString(latKey, "0")!!.toDouble(), sharedPrefs.getString(lngKey, "0")!!.toDouble()),
                LatLng(location.latitude+5, location.longitude)
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        //map.clear()
        MapRouter.showDirection(
            map,
            LatLng(sharedPrefs.getString(latKey, "0")!!.toDouble(), sharedPrefs.getString(lngKey, "0")!!.toDouble()),
            marker.position
        )
        return false
    }
}