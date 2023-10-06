package com.example.birdy

import com.google.gson.annotations.SerializedName

data class Hotspot(
    @SerializedName("locId") var locId: String? = null,
    @SerializedName("locName") var locName: String? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null,
    @SerializedName("countryCode") var countryCode: String? = null,
    @SerializedName("subnational1Code") var subnational1Code: String? = null,
    @SerializedName("latestObsDt") var latestObsDt: String? = null,
    @SerializedName("numSpeciesAllTime") var numSpeciesAllTime: Int? = null,
)
