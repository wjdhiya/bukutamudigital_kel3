package com.example.bukutamudigital.utils

import android.content.Context
import com.example.bukutamudigital.model.Tamu
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TamuStorage {

    private const val PREFS_NAME = "user_data_storage"
    private const val KEY_TAMU_LIST_PREFIX = "tamu_list_json_"

    private fun saveTamuList(context: Context, userId: String, tamuList: List<Tamu>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userSpecificKey = KEY_TAMU_LIST_PREFIX + userId
        val jsonString = Gson().toJson(tamuList)
        prefs.edit().putString(userSpecificKey, jsonString).apply()
    }

    fun getTamuList(context: Context, userId: String): MutableList<Tamu> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userSpecificKey = KEY_TAMU_LIST_PREFIX + userId
        val jsonString = prefs.getString(userSpecificKey, null)
        return if (jsonString != null) {
            val type = object : TypeToken<MutableList<Tamu>>() {}.type
            Gson().fromJson(jsonString, type)
        } else {
            mutableListOf()
        }
    }

    fun addTamu(context: Context, userId: String, tamu: Tamu) {
        val currentList = getTamuList(context, userId)
        currentList.add(0, tamu) // Tambahkan di awal daftar
        saveTamuList(context, userId, currentList)
    }

    fun updateTamu(context: Context, userId: String, updatedTamu: Tamu) {
        val currentList = getTamuList(context, userId)
        val index = currentList.indexOfFirst { it.id == updatedTamu.id }
        if (index != -1) {
            currentList[index] = updatedTamu
            saveTamuList(context, userId, currentList)
        }
    }

    fun deleteTamu(context: Context, userId: String, tamuId: Long) {
        val currentList = getTamuList(context, userId)
        currentList.removeAll { it.id == tamuId } // Hapus tamu dengan ID yang cocok
        saveTamuList(context, userId, currentList)
    }
}
