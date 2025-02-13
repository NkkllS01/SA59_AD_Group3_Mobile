package com.example.singnature.ExploreMenu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Park (
    val parkId: Int,
    val parkName: String,
    val parkRegion: String,
    val parkType: String,
    val imageUrl: String,
    val parkDescription: String,
    val openingHours: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable