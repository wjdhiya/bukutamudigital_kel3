package com.example.bukutamudigital.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukutamudigital.R
import com.example.bukutamudigital.adapter.OnItemClickListener
import com.example.bukutamudigital.adapter.TamuAdapter
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.GenericResponse
import com.example.bukutamudigital.model.Tamu
import com.example.bukutamudigital.model.TamuListResponse
import com.example.bukutamudigital.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.emptyList

// Implementasikan interface OnItemClickListener
class TamuListActivity : AppCompatActivity(), OnItemClickListener {

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

        recyclerView = findViewById(R.id.recyclerViewTamu)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tamuAdapter = TamuAdapter(emptyList(), this)
        recyclerView.adapter = tamuAdapter

        btnTambahTamu = findViewById(R.id.btnTambahTamu)

        btnTambahTamu.setOnClickListener {
            val intent = Intent(this, TamuFormActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchTamuData()
    }

    // --- Implementasi Listener dari TamuAdapter ---

    override fun onEditClick(tamu: Tamu) {
        val intent = Intent(this, TamuFormActivity::class.java).apply {
            putExtra("EXTRA_TAMU", tamu)
            putExtra("IS_EDIT_MODE", true)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(tamu: Tamu) {
        showDeleteConfirmationDialog(tamu)
    }

    private fun showDeleteConfirmationDialog(tamu: Tamu) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Tamu")
            .setMessage("Yakin ingin menghapus data tamu ${tamu.nama}?")
            .setPositiveButton("Hapus") { dialog, which ->
                deleteTamu(tamu.id.toString())
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteTamu(tamuId: String) {
        val token = session.getToken()
        if (token.isNullOrEmpty()) return

        ApiClient.apiService.deleteTamu("Bearer $token", tamuId).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@TamuListActivity, "Tamu berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchTamuData() // Muat ulang list
                } else {
                    // Cek response body untuk pesan error dari server
                    val errorMessage = response.body()?.message ?: "Gagal menghapus data"
                    Toast.makeText(this@TamuListActivity, "Gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@TamuListActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Fungsi Fetch Data ---

    private fun fetchTamuData() {
        val token = session.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        ApiClient.apiService.getTamu("Bearer $token").enqueue(object : Callback<TamuListResponse> {
            override fun onResponse(call: Call<TamuListResponse>, response: Response<TamuListResponse>) {
                if (response.isSuccessful) {
                    val tamuResponse = response.body()
                    if (tamuResponse != null && tamuResponse.success) {
                        tamuResponse.data?.let {
                            tamuAdapter.updateData(it)
                        } ?: tamuAdapter.updateData(emptyList())
                    } else {
                        tamuAdapter.updateData(emptyList())
                        val errorMessage = tamuResponse?.message ?: "Gagal memuat data tamu"
                        Toast.makeText(this@TamuListActivity, "Gagal memuat data: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    tamuAdapter.updateData(emptyList())
                    Toast.makeText(this@TamuListActivity, "Gagal memuat data. Kode: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TamuListResponse>, t: Throwable) {
                tamuAdapter.updateData(emptyList())
                Toast.makeText(this@TamuListActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}