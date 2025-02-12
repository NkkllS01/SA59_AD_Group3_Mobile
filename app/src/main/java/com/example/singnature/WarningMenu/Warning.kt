package com.example.singnature.WarningMenu

data class Warning (
    val warningId : Int,
    val source: String,
    val sightingId : Int?,
    val cluster : String?,
    val alertLevel : String?,
    val date : String,
    val description : String,
    val specie : String?
)
