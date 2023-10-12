package com.example.birdy

import java.util.Date

data class Sighting(
    val speciesCode: String,
    val sights: ArrayList<Sight>
)

data class Sight(
    val lat: Double,
    val lng: Double,
    val date: Date
)
