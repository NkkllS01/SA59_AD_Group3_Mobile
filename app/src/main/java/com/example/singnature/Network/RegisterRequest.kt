package com.example.singnature.Network


data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String?,
    val phone: String?,
    val subscribeWarning: Boolean = false,
    val subscribeNewsletter: Boolean = false
)
