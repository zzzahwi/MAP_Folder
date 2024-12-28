package com.example.musicstreamer

import DetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Search bar setup
        val searchBar = rootView.findViewById<EditText>(R.id.searchBar)
        setupSearchBar(searchBar)

        // RecyclerView setup
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SongAdapter { song ->
            // Navigate to DetailFragment
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            val detailFragment = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString("title", song.title)
                    putString("artist", song.artist)
                    putString("imageUrl", song.imageUrl)
                }
            }
            fragmentTransaction.replace(R.id.fragment_container, detailFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        recyclerView.adapter = adapter

        // Fetch and display data from Firestore
        fetchSongs()

        return rootView
    }

    private fun setupSearchBar(searchBar: EditText) {
        // Handle text input and "Enter" action
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchBar.text.toString().trim()
                performSearch(query)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        // Clear button for search bar (detect touch on drawableEnd)
        searchBar.setOnTouchListener { _, event ->
            if (event.rawX >= (searchBar.right - searchBar.compoundDrawables[2].bounds.width())) {
                searchBar.text.clear()
                fetchSongs() // Reset to full list when cleared
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun fetchSongs() {
        firestore.collection("songs")
            .get()
            .addOnSuccessListener { result ->
                val songs = result.map { document ->
                    Song(
                        title = document.getString("title") ?: "Unknown Title",
                        artist = document.getString("artist") ?: "Unknown Artist",
                        imageUrl = document.getString("imageUrl") ?: "",
                    )
                }
                adapter.submitList(songs)
            }
            .addOnFailureListener { exception ->
                // Handle error (e.g., Toast message)
            }
    }

    private fun performSearch(query: String) {
        firestore.collection("songs")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", "$query\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val filteredSongs = result.map { document ->
                    Song(
                        title = document.getString("title") ?: "Unknown Title",
                        artist = document.getString("artist") ?: "Unknown Artist",
                        imageUrl = document.getString("imageUrl") ?: "",
                    )
                }
                adapter.submitList(filteredSongs)
            }
            .addOnFailureListener { exception ->
                // Handle error (e.g., Toast message)
            }
    }

    private fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
