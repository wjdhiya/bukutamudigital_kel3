package com.example.bukutamudigital.api

import com.example.bukutamudigital.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register.php")
    fun register(@Body body: Map<String, String>): Call<GenericResponse>

    @POST("login.php")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @GET("get_tamu.php")
    fun getTamu(@Query("token") token: String): Call<TamuListResponse>

    // 1. Tambahkan anotasi @FormUrlEncoded di atas fungsi
    @FormUrlEncoded
    @POST("add_tamu.php")
    fun addTamu(
        @Header("Authorization") token: String,    // Token tetap di header
        // 2. Ganti @Body dengan tiga @Field terpisah
        @Field("nama") nama: String,
        @Field("instansi") instansi: String,
        @Field("keperluan") keperluan: String
    ): Call<GenericResponse>

    @POST("edit_tamu.php")
    fun editTamu(@Body body: Map<String, String>): Call<GenericResponse>

    @POST("delete_tamu.php")
    fun deleteTamu(@Body body: Map<String, String>): Call<GenericResponse>
}
