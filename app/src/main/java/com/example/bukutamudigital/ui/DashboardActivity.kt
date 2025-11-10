package com.example.bukutamudigital.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.utils.SessionManager

class DashboardActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        session = SessionManager(this)

        val btnLihatTamu = findViewById<Button>(R.id.btnLihatTamu)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLihatTamu.setOnClickListener {
            startActivity(Intent(this, TamuListActivity::class.java))
        }

        btnLogout.setOnClickListener {
            session.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
