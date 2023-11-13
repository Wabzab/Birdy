package com.example.birdy.notebook

import com.example.birdy.observations.Species
import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("species") val species: Species,
    @SerializedName("checked") val checked: Boolean
)
