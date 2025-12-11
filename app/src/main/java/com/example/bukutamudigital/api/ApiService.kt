package com.example.bukutamudigital.api

import com.example.bukutamudigital.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("register.php")
    fun register(@Body body: Map<String, String>): Call<GenericResponse>

    @POST("login.php")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    // Endpoint baru untuk login via Google
    @POST("login_google.php")
    fun loginWithGoogle(@Body body: Map<String, String>): Call<LoginResponse>

    // Menggunakan Header untuk otorisasi yang konsisten
    @GET("get_tamu.php")
    fun getTamu(@Header("Authorization") token: String): Call<TamuListResponse>

    @FormUrlEncoded
    @POST("add_tamu.php")
    fun addTamu(
        @Header("Authorization") token: String,
        @Field("nama") nama: String,
        @Field("instansi") instansi: String,
        @Field("keperluan") keperluan: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("edit_tamu.php")
    fun editTamu(
        @Header("Authorization") token: String,
        @Field("id") id: String, // ID diperlukan untuk edit
        @Field("nama") nama: String,
        @Field("instansi") instansi: String,
        @Field("keperluan") keperluan: String
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("delete_tamu.php")
    fun deleteTamu(
        @Header("Authorization") token: String,
        @Field("id") id: String // ID diperlukan untuk hapus
    ): Call<GenericResponse>
}