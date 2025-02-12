package com.example.singnature.Network

data class LoginResponse(
    val userId: Int,
    val username: String,
    val email: String?,
    val mobile: String?,
    val warning: Boolean,
    val newsletter: Boolean
)

