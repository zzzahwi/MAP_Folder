package com.example.musicstreamer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        firestore = FirebaseFirestore.getInstance()

        // Back Button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Log Out Button
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val email = intent.getStringExtra("email") ?: "unknown@example.com"
            confirmLogout(email)
        }
    }

    private fun confirmLogout(email: String) {
        // Show a confirmation dialog
        // If confirmed, delete user data from Firestore
        firestore.collection("users").document(email).delete().addOnSuccessListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            // Handle error if needed
        }
    }
}
