package com.example.bukutamudigital.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tamu(
    // Gunakan System.currentTimeMillis() untuk ID unik sederhana
    val id: Long = System.currentTimeMillis(), 
    val nama: String,
    val instansi: String,
    val keperluan: String
) : Parcelable