package com.example.singnature.WildlifeMenu

import com.google.gson.annotations.SerializedName

data class SpeciesCategory(
    @SerializedName("categoryId") val id: Int,
    @SerializedName("categoryName") val name: String,
    @SerializedName("imageUrl") val imageUrl: String,
)
