package com.example.bukutamudigital.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.GenericResponse
import com.example.bukutamudigital.model.Tamu
import com.example.bukutamudigital.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TamuFormActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var etNama: EditText
    private lateinit var etInstansi: EditText
    private lateinit var etKeperluan: EditText
    private lateinit var btnSimpan: Button

    private var isEditMode = false
    private var tamuIdToEdit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_form)

        session = SessionManager(this)

        etNama = findViewById(R.id.etNama)
        etInstansi = findViewById(R.id.etInstansi)
        etKeperluan = findViewById(R.id.etKeperluan)
        btnSimpan = findViewById(R.id.btnSimpan)

        setupMode()

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val instansi = etInstansi.text.toString().trim()
            val keperluan = etKeperluan.text.toString().trim()

            if (nama.isEmpty() || instansi.isEmpty() || keperluan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 💡 Menggunakan let untuk akses yang lebih aman dan non-null
            if (isEditMode) {
                tamuIdToEdit?.let { id ->
                    editDataTamu(id, nama, instansi, keperluan)
                } ?: Toast.makeText(this, "ID Tamu tidak ditemukan untuk diedit.", Toast.LENGTH_SHORT).show()
            } else {
                simpanDataTamu(nama, instansi, keperluan)
            }
        }
    }

    private fun setupMode() {
        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)

        if (isEditMode) {
            // Pastikan Anda sudah mengimplementasikan Parcelable di model Tamu
            val tamuToEdit = intent.getParcelableExtra<Tamu>("EXTRA_TAMU")

            if (tamuToEdit != null) {
                supportActionBar?.title = "Edit Tamu: ${tamuToEdit.nama}"
                tamuIdToEdit = tamuToEdit.id.toString()
                btnSimpan.text = "UBAH DATA"

                etNama.setText(tamuToEdit.nama)
                etInstansi.setText(tamuToEdit.instansi)
                etKeperluan.setText(tamuToEdit.keperluan)
            } else {
                isEditMode = false
                supportActionBar?.title = "Tambah Tamu Baru"
            }
        } else {
            supportActionBar?.title = "Tambah Tamu Baru"
        }
    }

    private fun simpanDataTamu(nama: String, instansi: String, keperluan: String) {
        val token = session.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            return
        }

        ApiClient.apiService.addTamu("Bearer $token", nama, instansi, keperluan).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        Toast.makeText(this@TamuFormActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                        finish()
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

    private fun editDataTamu(id: String, nama: String, instansi: String, keperluan: String) {
        val token = session.getToken()

        // 💡 PERBAIKAN 1: Pindahkan pengecekan token ke atas dan jadikan guard clause.
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show()
            return
        }

        // 💡 PERBAIKAN 2: Hapus panggilan API yang duplikat. Hanya ada satu enqueue.
        ApiClient.apiService.editTamu("Bearer $token", id, nama, instansi, keperluan)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            Toast.makeText(this@TamuFormActivity, apiResponse.message, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // Pesan "invalid" dari server akan ditampilkan di sini
                            Toast.makeText(this@TamuFormActivity, "Gagal mengubah: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
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