package com.example.bukutamudigital.ui

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.model.Tamu
import com.example.bukutamudigital.utils.TamuStorage
import com.google.firebase.auth.FirebaseAuth

class AddTamuActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var isEditMode = false
    private var existingTamu: Tamu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tamu)

        firebaseAuth = FirebaseAuth.getInstance()

        val etNama = findViewById<EditText>(R.id.etNama)
        val etInstansi = findViewById<EditText>(R.id.etInstansi)
        val etKeperluan = findViewById<EditText>(R.id.etKeperluan)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        // Cek apakah ada data tamu yang dikirim dari intent (mode edit)
        existingTamu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_TAMU_DATA", Tamu::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_TAMU_DATA")
        }

        if (existingTamu != null) {
            isEditMode = true
            supportActionBar?.title = "Edit Tamu"
            btnSimpan.text = "Update"

            etNama.setText(existingTamu!!.nama)
            etInstansi.setText(existingTamu!!.instansi)
            etKeperluan.setText(existingTamu!!.keperluan)
        } else {
            isEditMode = false
            supportActionBar?.title = "Tambah Tamu Baru"
            btnSimpan.text = "Simpan"
        }

        btnSimpan.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Sesi pengguna tidak ditemukan.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val userId = currentUser.uid
            val nama = etNama.text.toString().trim()
            val instansi = etInstansi.text.toString().trim()
            val keperluan = etKeperluan.text.toString().trim()

            if (nama.isBlank() || instansi.isBlank() || keperluan.isBlank()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                // Mode Edit: Buat objek baru dengan ID yang sama
                val updatedTamu = existingTamu!!.copy(nama = nama, instansi = instansi, keperluan = keperluan)
                TamuStorage.updateTamu(this, userId, updatedTamu)
                Toast.makeText(this, "Tamu berhasil diupdate", Toast.LENGTH_SHORT).show()
            } else {
                // Mode Tambah: Buat objek baru dengan ID baru
                val tamuBaru = Tamu(nama = nama, instansi = instansi, keperluan = keperluan)
                TamuStorage.addTamu(this, userId, tamuBaru)
                Toast.makeText(this, "Tamu berhasil disimpan", Toast.LENGTH_SHORT).show()
            }

            finish() // Kembali ke layar daftar tamu
        }
    }
}
