package com.example.singnature.WildlifeMenu

import com.example.singnature.Network.DateJsonAdapter
import com.google.gson.annotations.JsonAdapter
import java.util.Date

data class Sightings (
    val sightingId: Int,
    val userId: Int,
    @JsonAdapter(DateJsonAdapter::class)
    val date: Date,
    val specieId: Int,
    val details: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val status: String
)