package com.example.musicstreamer

import DetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class FavoritesFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        val rootView = inflater.inflate(R.layout.fragment_favorites, container, false)

        val containerLayout = rootView.findViewById<LinearLayout>(R.id.containerLayoutFavorites)

        // Mengambil data dari koleksi "favorites" di Firestore
        firestore.collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.getString("title") ?: "Unknown Title"
                    val artist = document.getString("artist") ?: "Unknown Artist"
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val lyrics = document.getString("lyrics") ?: ""

                    val coverImageView = ImageView(requireContext())
                    Picasso.get().load(imageUrl).into(coverImageView)

                    val titleTextView = TextView(requireContext())
                    titleTextView.text = title

                    val artistTextView = TextView(requireContext())
                    artistTextView.text = artist

                    val lyricsTextView = TextView(requireContext())
                    lyricsTextView.text = lyrics

                    // Membuat layout untuk setiap lagu
                    val songLayout = LinearLayout(requireContext())
                    songLayout.orientation = LinearLayout.VERTICAL
                    songLayout.addView(coverImageView)
                    songLayout.addView(titleTextView)
                    songLayout.addView(artistTextView)
                    songLayout.addView(lyricsTextView)

                    songLayout.setOnClickListener {
                        // Navigasi ke DetailFragment dengan mengirim data menggunakan Bundle
                        val bundle = Bundle().apply {
                            putString("title", title)
                            putString("artist", artist)
                            putString("imageUrl", imageUrl)
                            putString("lyrics", lyrics)
                        }

                        val detailFragment = DetailFragment().apply {
                            arguments = bundle
                        }

                        // Menavigasi ke DetailFragment
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack(null) // Tambahkan ke back stack agar bisa di-back
                            .commit()
                    }

                    containerLayout.addView(songLayout)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        return rootView
    }
}
