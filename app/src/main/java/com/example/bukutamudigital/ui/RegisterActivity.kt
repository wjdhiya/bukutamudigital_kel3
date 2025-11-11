package com.example.bukutamudigital.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "username" to username,
                "password" to password
            )

            ApiClient.apiService.register(body)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val res = response.body()!!
                            val message = res.message ?: "Terjadi kesalahan"

                            // Cek apakah sukses ATAU pesan mengandung kata 'Berhasil'
                            // (untuk mengatasi masalah inkonsistensi server)
                            val isSuccess = res.success || message.contains("Berhasil", ignoreCase = true)

                            if (isSuccess) {
                                // 1. Kasus Berhasil
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registrasi Berhasil", // Sesuai permintaan Anda
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                // 2. Kasus Gagal
                                val errorMessage: String

                                // Cek pesan untuk kasus "Username sudah dipakai"
                                if (message.contains("sudah terdaftar", ignoreCase = true) || message.contains("already exists", ignoreCase = true) || message.contains("sudah dipakai", ignoreCase = true)) {
                                    errorMessage = "Username sudah dipakai" // Sesuai permintaan Anda
                                } else {
                                    errorMessage = "Gagal: $message"
                                }

                                Toast.makeText(
                                    this@RegisterActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Respons HTTP tidak 2xx (misalnya 400, 500)
                            Toast.makeText(
                                this@RegisterActivity,
                                "Gagal: Respons server tidak valid (Kode: ${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Kesalahan koneksi: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}