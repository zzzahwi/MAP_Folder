package com.example.musicstreamer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Splash screen delay (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the user is logged in (example using SharedPreferences)
            val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

            if (isLoggedIn) {
                // Navigate to HomeActivity if logged in
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // Navigate to RegisterActivity if not logged in
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            finish()
        }, 2000) // 2000ms = 2 seconds
    }
}
