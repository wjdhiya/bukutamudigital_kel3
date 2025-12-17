package com.example.bukutamudigital.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bukutamudigital.R
import com.example.bukutamudigital.api.ApiClient
import com.example.bukutamudigital.model.LoginResponse
import com.example.bukutamudigital.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var session: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "FIREBASE_AUTH"

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "Gagal mendapatkan ID Token Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Login Google Gagal: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManager(this)
        firebaseAuth = FirebaseAuth.getInstance()

        if (session.isLoggedIn()) {
            goToDashboard()
            return
        }

        val etUser = findViewById<EditText>(R.id.etUsername)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = findViewById<SignInButton>(R.id.btnGoogleSignIn)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
            val username = etUser.text.toString().trim()
            val password = etPass.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val body = mapOf("username" to username, "password" to password)
            loginUser(body)
        }

        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        // SIMPAN SESI LOKAL SETELAH LOGIN GOOGLE BERHASIL
                        session.saveSession(0, firebaseUser.uid) // user_id 0, token diisi UID firebase
                        Toast.makeText(this, "Login Google Berhasil.", Toast.LENGTH_SHORT).show()
                        goToDashboard()
                    } else {
                        Toast.makeText(this, "Gagal mendapatkan data user dari Firebase.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Autentikasi Gagal.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser(body: Map<String, String>) {
        ApiClient.apiService.login(body).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val res = response.body()
                if (res != null && res.success && res.data != null) {
                    session.saveSession(res.data.user_id, res.data.token)
                    Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                } else {
                    Toast.makeText(this@LoginActivity, res?.message ?: "Login gagal, username atau password salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
