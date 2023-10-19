package com.example.birdy

import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("routes") var routes: Any? = null,
    @SerializedName("fallbackInfo") var fallbackInfo: Any? = null,
    @SerializedName("geocodingResults") var geocodingResults: Any? = null
)
