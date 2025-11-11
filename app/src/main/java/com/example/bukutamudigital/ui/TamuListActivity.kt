package com.example.bukutamudigital.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bukutamudigital.R
import com.example.bukutamudigital.adapter.TamuAdapter
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.TamuListResponse
import com.example.bukutamudigital.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TamuListActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var tamuAdapter: TamuAdapter

    private lateinit var btnTambahTamu: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_list)

        session = SessionManager(this)
        supportActionBar?.title = "Daftar Tamu"

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerViewTamu)
        tamuAdapter = TamuAdapter(emptyList()) // Mulai dengan daftar kosong
        recyclerView.adapter = tamuAdapter

        btnTambahTamu = findViewById(R.id.btnTambahTamu)

        // 2. Beri aksi ketika tombol diklik.
        btnTambahTamu.setOnClickListener {
            // Buat Intent untuk pindah dari activity ini ke TamuFormActivity.
            val intent = Intent(this, TamuFormActivity::class.java)
            // Mulai activity baru.
            startActivity(intent)
        }
        // Panggil fungsi untuk mengambil data dari server
        fetchTamuData()
    }

    override fun onResume() {
        super.onResume()
        // Panggil ulang API setiap kali activity ini kembali dibuka
        // Berguna jika ada data baru yang ditambahkan dari TamuFormActivity
        fetchTamuData()
    }
    private fun fetchTamuData() {
        val token = session.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            // Jika token tidak ada, kembali ke halaman login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Panggil API getTamu menggunakan Retrofit
        ApiClient.apiService.getTamu("Bearer $token").enqueue(object : Callback<TamuListResponse> {
            override fun onResponse(call: Call<TamuListResponse>, response: Response<TamuListResponse>) {
                if (response.isSuccessful) {
                    val tamuResponse = response.body()
                    if (tamuResponse != null && tamuResponse.success) {
                        // Jika data berhasil didapat dan tidak null, perbarui data di adapter
                        tamuResponse.data?.let {
                            tamuAdapter.updateData(it)
                        }
                    } else {
                        Toast.makeText(this@TamuListActivity, "Gagal memuat data: ${tamuResponse?.data}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TamuListActivity, "Gagal memuat data. Kode: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TamuListResponse>, t: Throwable) {
                Toast.makeText(this@TamuListActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
