package com.example.singnature.WildlifeMenu

import com.example.singnature.Network.DateJsonAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.JsonAdapter
import com.google.maps.android.clustering.ClusterItem
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
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String = "Wildlife Sightings"
    override fun getSnippet(): String = ""
    override fun getZIndex(): Float? {
        return 1.0f
    }
}