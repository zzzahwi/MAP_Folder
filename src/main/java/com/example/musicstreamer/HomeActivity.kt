package com.example.musicstreamer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firestore = FirebaseFirestore.getInstance()

        // Set HomeFragment as default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Setup Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                else -> false
            }
        }

        // Setup user icon click
        val userIcon = findViewById<ImageView>(R.id.userIcon)
        userIcon.setOnClickListener {
            // Navigate to UserActivity
            Log.d("HomeActivity", "User Icon Clicked") // Debug Log
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        // (Optional) Handle logo click if needed
        val appLogo = findViewById<ImageView>(R.id.appLogo)
        appLogo.setOnClickListener {
            // Optional: Perform an action when the logo is clicked
        }
    }

    // Function to load fragments dynamically
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
