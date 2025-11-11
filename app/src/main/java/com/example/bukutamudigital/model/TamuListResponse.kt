package com.example.bukutamudigital.model

data class TamuListResponse(
    val success: Boolean,
    val message: String?, // Dibuat nullable untuk null safety
    val data: List<Tamu>?
)