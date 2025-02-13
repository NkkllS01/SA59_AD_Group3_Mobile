package com.example.singnature.Network


data class RegisterResponse(
    val userId: Int,
    val username: String,
    val email: String?,
    val mobile: String?,
    val warning: Boolean,
    val newsletter: Boolean
)
