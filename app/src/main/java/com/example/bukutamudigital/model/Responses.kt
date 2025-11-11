package com.example.bukutamudigital.model

data class GenericResponse(
    val success: Boolean,
    val message: String? // 💡 Disarankan String? untuk null safety
)

data class LoginData(
    val user_id: Int,
    val token: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?, // 💡 Disarankan String? untuk null safety
    val data: LoginData?
)

// Catatan: Anda juga harus memastikan model TamuListResponse
// (yang tidak ditampilkan di sini) memiliki message: String?
// agar sesuai dengan perbaikan di TamuListActivity.kt