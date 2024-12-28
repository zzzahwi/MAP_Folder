package com.example.musicstreamer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest


class LoginActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Hash password sebelum dibandingkan
                val hashedPassword = hashPassword(password)

                // Retrieve user document from Firestore
                firestore.collection("users")
                    .document(email)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val storedPassword = document.getString("password")
                            if (storedPassword == hashedPassword) {
                                // Save login state (example using SharedPreferences)
                                val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                sharedPreferences.edit().putBoolean("IS_LOGGED_IN", true).apply()

                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Fungsi hash password yang sama seperti di RegisterActivity
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

