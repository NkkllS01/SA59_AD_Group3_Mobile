package com.example.singnature.WildlifeMenu

import android.os.Parcelable
import com.example.singnature.Network.DateJsonAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.JsonAdapter
import com.google.maps.android.clustering.ClusterItem
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Sightings (
    val sightingId: Int,
    val userId: Int,
    var userName: String,
    @JsonAdapter(DateJsonAdapter::class)
    val date: Date,
    val specieId: Int,
    val specieName: String,
    val details: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    //val status: SightingStatus
) : ClusterItem, Parcelable {
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String = specieName
    override fun getSnippet(): String = "Reported by: " + userName
    override fun getZIndex(): Float? {
        return 1.0f
    }
//    enum class SightingStatus {
//        Active,
//        Inactive
//    }
}