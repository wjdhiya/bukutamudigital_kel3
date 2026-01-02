package com.example.bukutamudigital.ui

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
import com.example.bukutamudigital.model.Tamu
import com.example.bukutamudigital.utils.TamuStorage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class TamuListActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tamuAdapter: TamuAdapter
    private lateinit var fabAddTamu: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tamu_list)

        firebaseAuth = FirebaseAuth.getInstance()
        supportActionBar?.title = "Daftar Tamu (Lokal)"

        //recyclerView = findViewById(R.id.recyclerViewTamu)
        fabAddTamu = findViewById(R.id.btnTambahTamu)

        setupRecyclerView()

        fabAddTamu.setOnClickListener {
            // Buka AddTamuActivity dalam mode TAMBAH (tanpa mengirim data)
            val intent = Intent(this, AddTamuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTamuFromStorage()
    }

    private fun setupRecyclerView() {
        tamuAdapter = TamuAdapter(mutableListOf(), this)
        recyclerView.apply {
            adapter = tamuAdapter
            layoutManager = LinearLayoutManager(this@TamuListActivity)
        }
    }

    private fun loadTamuFromStorage() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            handleUserNotLoggedIn()
            return
        }
        val userId = currentUser.uid
        val tamuList = TamuStorage.getTamuList(this, userId)
        tamuAdapter.updateData(tamuList)
    }

    override fun onDeleteClick(tamu: Tamu) {
        showDeleteConfirmationDialog(tamu)
    }

    // Implementasikan fungsi onEditClick dari interface
    override fun onEditClick(tamu: Tamu) {
        // Buka AddTamuActivity dalam mode EDIT dengan mengirim data tamu
        val intent = Intent(this, AddTamuActivity::class.java).apply {
            putExtra("EXTRA_TAMU_DATA", tamu)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(tamu: Tamu) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            handleUserNotLoggedIn()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Hapus Tamu")
            .setMessage("Yakin ingin menghapus data tamu ${tamu.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                TamuStorage.deleteTamu(this, currentUser.uid, tamu.id)
                Toast.makeText(this, "Tamu berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadTamuFromStorage()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun handleUserNotLoggedIn() {
        Toast.makeText(this, "Sesi pengguna tidak ditemukan, silakan login ulang.", Toast.LENGTH_LONG).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
