package com.example.bukutamudigital.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("bukutamu_prefs", Context.MODE_PRIVATE)

    fun saveSession(userId: Int, token: String) {
        prefs.edit().putInt("user_id", userId).putString("token", token).apply()
    }
    fun clearSession() {
        prefs.edit().clear().apply()
    }
    fun isLoggedIn(): Boolean = prefs.getString("token", null) != null
    fun getToken(): String? = prefs.getString("token", null)
    fun getUserId(): Int = prefs.getInt("user_id", 0)
}
