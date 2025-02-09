package com.example.singnature.Network

data class LoginResponse(
    val userId: Int,
    val username: String,
    val email: String?,
    val phone: String?,
    val subscribeWarning: Boolean,
    val subscribeNewsletter: Boolean
)

