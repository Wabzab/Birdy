package com.example.birdy

import java.util.Date

data class Sighting(
    val species: String,
    val lat: Double,
    val lng: Double,
    val date: Date
)

data class Sight(
    val lat: Double,
    val lng: Double,
    val date: Date
)
