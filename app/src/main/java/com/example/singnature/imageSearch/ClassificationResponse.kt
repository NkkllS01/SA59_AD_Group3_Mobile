package com.example.singnature.imageSearch

import com.google.gson.annotations.SerializedName

data class ClassificationResponse(
    @SerializedName("class") val className : String,
    val probabilities: Probabilities
)
