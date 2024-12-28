package com.example.musicstreamer

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
class RegisterActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validasi input email dan password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash password sebelum disimpan di Firestore
            val hashedPassword = hashPassword(password)
            val user = hashMapOf(
                "email" to email,
                "password" to hashedPassword
            )

            // Membuat ProgressBar di dalam AlertDialog
            val progressBar = ProgressBar(this)
            progressBar.isIndeterminate = true  // Menandakan proses yang tidak bisa diprediksi

            val dialog = AlertDialog.Builder(this)
                .setTitle("Registering...")
                .setView(progressBar)
                .setCancelable(false)  // Tidak bisa dibatalkan dengan menekan luar dialog
                .create()

            dialog.show()

            // Cek apakah email sudah terdaftar di Firestore
            firestore.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        dialog.dismiss()  // Menutup dialog ketika email sudah terdaftar
                        Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        // Lanjutkan proses registrasi
                        firestore.collection("users")
                            .document(email)
                            .set(user)
                            .addOnSuccessListener {
                                dialog.dismiss()  // Menutup dialog saat registrasi sukses
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                dialog.dismiss()  // Menutup dialog saat gagal
                                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    dialog.dismiss()  // Menutup dialog jika terjadi error
                    Toast.makeText(this, "Error checking email: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    // Fungsi navigateToLogin untuk menangani klik pada teks Login
    fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}