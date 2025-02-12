package com.example.singnature.Network


data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String?,
    val mobile: String?,
    val warning: Boolean = false,
    val newsletter: Boolean = false
)
