package com.example.bukutamudigital.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    private lateinit var session: SessionManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        session = SessionManager(this)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inisialisasi Tombol sesuai ID di XML
        val btnTambahTamu = findViewById<Button>(R.id.btnTambahTamu)
        val btnLihatTamu = findViewById<Button>(R.id.btnLihatTamu)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Fungsi Tombol Tambah Tamu (Pindah ke AddTamuActivity)
        btnTambahTamu.setOnClickListener {
            val intent = Intent(this, AddTamuActivity::class.java)
            startActivity(intent)
        }

        // Fungsi Tombol Lihat Daftar Tamu
        btnLihatTamu.setOnClickListener {
            startActivity(Intent(this, TamuListActivity::class.java))
        }

        // Fungsi Tombol Logout
        btnLogout.setOnClickListener {
            session.clearSession()
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}