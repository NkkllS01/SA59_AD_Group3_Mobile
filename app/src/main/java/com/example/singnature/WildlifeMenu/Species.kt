package com.example.singnature.WildlifeMenu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Species (
    val specieId: Int,
    val specieName: String,
    val description: String,
    val highlights: String,
    val categoryId: Int,

): Parcelable

