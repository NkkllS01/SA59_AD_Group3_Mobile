package com.example.singnature.WildlifeMenu.imageSearch

import com.google.gson.annotations.SerializedName

data class ClassificationResponse (
    @SerializedName("species") val species : List<String>
)
