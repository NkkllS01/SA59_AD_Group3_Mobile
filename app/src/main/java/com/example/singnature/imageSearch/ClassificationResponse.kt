package com.example.singnature.imageSearch

import com.google.gson.annotations.SerializedName

data class ClassificationResponse(
    @SerializedName("species") val species : List<String>
)
