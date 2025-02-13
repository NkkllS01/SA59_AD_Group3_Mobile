package com.example.singnature.WarningMenu

import java.util.Date

data class Warning (
    val warningId : Int,
    val source: String,
    val sightingId : Int?,
    val cluster : String?,
    val alertLevel : String?,
)
