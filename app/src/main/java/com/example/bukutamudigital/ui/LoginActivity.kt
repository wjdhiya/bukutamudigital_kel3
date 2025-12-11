package com.example.bukutamudigital.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import Log untuk debugging
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

// Pastikan Anda sudah punya DashboardActivity di package com.example.bukutamudigital.ui
// import com.example.bukutamudigital.ui.DashboardActivity // (Jika sudah ada di package yang sama, tidak perlu import eksplisit)

class LoginActivity : AppCompatActivity() {
    private lateinit var session: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    // Tag untuk Logcat, memudahkan debugging
    private val TAG = "FIREBASE_AUTH_DEBUG"

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Berhasil mendapatkan Account dari Google
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken

                if (idToken != null) {
                    Log.d(TAG, "Google ID Token berhasil diterima. Memulai Auth Firebase...")
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e(TAG, "ID Token NULL setelah Google Sign-In berhasil.")
                    Toast.makeText(this, "Gagal mendapatkan ID Token dari Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                // Kegagalan Google Sign-In (misalnya dibatalkan pengguna)
                Log.e(TAG, "Google sign in failed: Status Code ${e.statusCode}", e)
                Toast.makeText(this, "Login Google Gagal: Kode ${e.statusCode}", Toast.LENGTH_SHORT).show()
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

        // Inisialisasi View
        val etUser = findViewById<EditText>(R.id.etUsername)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = findViewById<SignInButton>(R.id.btnGoogleSignIn)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Konfigurasi Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Menggunakan R.string.default_web_client_id yang sudah Anda isi
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Listener Login Biasa
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

        // Listener Google Sign-In
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        // Listener Register
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // FUNGSI UTAMA: Mengautentikasi ID Token Google ke Firebase
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // BERHASIL LOGIN KE FIREBASE
                    Log.d(TAG, "Autentikasi Firebase Berhasil. User ID: ${firebaseAuth.currentUser?.uid}")

                    Toast.makeText(this, "Login Google Berhasil (via Firebase)!", Toast.LENGTH_SHORT).show()
                    goToDashboard()

                } else {
                    // GAGAL LOGIN KE FIREBASE
                    // Log error secara detail ke Logcat
                    Log.e(TAG, "Firebase auth failed: ${task.exception?.message}", task.exception)

                    Toast.makeText(this, "Autentikasi Firebase Gagal! Cek Logcat.", Toast.LENGTH_LONG).show()
                }
            }
    }

    // FUNGSI loginUser dan goToDashboard tetap sama
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