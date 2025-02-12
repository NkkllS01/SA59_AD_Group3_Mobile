package com.example.singnature.Network

data class UpdateProfileRequest(
    val userId: Int?,
    val email: String?,
    val mobile: String?,
    val warning: Boolean,
    val newsletter: Boolean
)
