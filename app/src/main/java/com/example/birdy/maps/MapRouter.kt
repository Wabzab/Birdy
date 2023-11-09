package com.example.birdy.maps

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.birdy.BuildConfig
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.concurrent.Executors

object MapRouter {

    var polylines: ArrayList<Polyline> = ArrayList()

    private fun getDirectionURL(origin: LatLng, dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=walking" +
                "&key=${BuildConfig.MAPS_API_KEY}"
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    public fun showDirection(map: GoogleMap, origin: LatLng, dest:LatLng){
        val url = getDirectionURL(origin, dest)
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body().string()

            val result = ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                Log.d("ROUTES", "$e")
            }

            handler.post {
                val lineOption = PolylineOptions()
                for (i in result.indices){
                    lineOption.addAll(result[i])
                    lineOption.width(10f)
                    lineOption.color(Color.BLUE)
                    lineOption.geodesic(true)
                    lineOption.pattern(listOf(Dot()))
                }
                for(p in polylines) {
                    p.isVisible = false
                    polylines.remove(p)
                }
                val polyline = map.addPolyline(lineOption)
                polylines.add(polyline)
            }
        }
    }



}