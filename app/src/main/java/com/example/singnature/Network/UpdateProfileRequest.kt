package com.example.singnature.Network

data class UpdateProfileRequest(
    val email: String?,
    val phone: String?,
    val subscribeWarning: Boolean,
    val subscribeNewsletter: Boolean
)
