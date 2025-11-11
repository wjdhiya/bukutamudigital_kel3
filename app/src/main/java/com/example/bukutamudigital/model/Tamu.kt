package com.example.bukutamudigital.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tamu(
    val id: Int, // Asumsi ID tamu bertipe Int di database
    val nama: String,
    val instansi: String,
    val keperluan: String,
    val created_at: String
) : Parcelable