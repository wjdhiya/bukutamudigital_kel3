package com.example.bukutamudigital.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.GenericResponse
import com.example.bukutamudigital.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TamuFormActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_form)

        session = SessionManager(this)
        supportActionBar?.title = "Tambah Tamu Baru"

        val etNama = findViewById<EditText>(R.id.etNama)
        val etInstansi = findViewById<EditText>(R.id.etInstansi)
        val etKeperluan = findViewById<EditText>(R.id.etKeperluan)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val instansi = etInstansi.text.toString().trim()
            val keperluan = etKeperluan.text.toString().trim()

            if (nama.isEmpty() || instansi.isEmpty() || keperluan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            simpanDataTamu(nama, instansi, keperluan)
        }
    }

    private fun simpanDataTamu(nama: String, instansi: String, keperluan: String) {
        val token = session.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            return
        }

        // HANYA ADA SATU BLOK PANGGILAN API YANG LENGKAP DAN BENAR
        // Panggil API dengan EMPAT parameter: token, nama, instansi, dan keperluan
        ApiClient.apiService.addTamu("Bearer $token", nama, instansi, keperluan).enqueue(object : Callback<GenericResponse> {

            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        Toast.makeText(this@TamuFormActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke halaman daftar tamu
                    } else {
                        Toast.makeText(this@TamuFormActivity, "Gagal menyimpan: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TamuFormActivity, "Gagal terhubung ke server. Kode: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@TamuFormActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
