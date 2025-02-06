package com.example.singnature.WildlifeMenu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Species (
    val SpecieId: Int,
    val SpecieName: String,
    val Description: String,
    val Highlights: String
): Parcelable