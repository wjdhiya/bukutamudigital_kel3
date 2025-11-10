package com.example.bukutamudigital.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R

class TamuFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_form)

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

            Toast.makeText(this, "Tamu $nama disimpan (dummy)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
