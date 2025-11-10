package com.example.bukutamudigital.model

data class GenericResponse(
    val success: Boolean,
    val message: String
)

data class LoginData(
    val user_id: Int,
    val token: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

