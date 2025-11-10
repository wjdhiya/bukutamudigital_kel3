package com.example.bukutamudigital.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R

class TamuListActivity : AppCompatActivity() {
    private val dataTamu = mutableListOf("Andi", "Budi", "Citra")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_list)

        val listView = findViewById<ListView>(R.id.listTamu)
        val btnTambahTamu = findViewById<Button>(R.id.btnTambahTamu)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataTamu)
        listView.adapter = adapter

        btnTambahTamu.setOnClickListener {
            startActivity(Intent(this, TamuFormActivity::class.java))
        }
    }
}
