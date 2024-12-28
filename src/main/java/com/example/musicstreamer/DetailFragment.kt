import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicstreamer.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // Inflate layout fragment
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)

        // Mendapatkan data dari arguments (Bundle)
        val title = arguments?.getString("title") ?: "Unknown Title"
        val artist = arguments?.getString("artist") ?: "Unknown Artist"
        val imageUrl = arguments?.getString("imageUrl") ?: ""
        val lyrics = arguments?.getString("lyrics") ?: ""  // URL untuk gambar lirik

        // Mengatur tampilan UI
        val titleTextView = rootView.findViewById<TextView>(R.id.textViewTitle)
        val artistTextView = rootView.findViewById<TextView>(R.id.textViewArtist)
        val lyricsImageView = rootView.findViewById<ImageView>(R.id.imageViewLyrics) // ImageView untuk lirik
        val coverImageView = rootView.findViewById<ImageView>(R.id.imageViewCover)
        val addToFavoritesButton = rootView.findViewById<Button>(R.id.buttonAddToFavorites)

        // Set title dan artist di TextView
        titleTextView.text = title
        artistTextView.text = artist

        // Menampilkan gambar cover menggunakan Picasso
        Picasso.get().load(imageUrl).into(coverImageView)

        // Memuat lirik dari URL jika ada (sekarang sebagai gambar)
        loadLyricsFromUrl(lyrics, lyricsImageView)

        // Menambahkan lagu ke koleksi "favorites"
        addToFavoritesButton.setOnClickListener {
            addToFavorites(title, artist, imageUrl, lyrics)
        }

        return rootView
    }

    // Fungsi untuk memuat lirik dari URL (sekarang dalam bentuk gambar)
    private fun loadLyricsFromUrl(lyrics: String, lyricsImageView: ImageView) {
        if (lyrics.isNotEmpty()) {
            // Menampilkan gambar lirik menggunakan Picasso
            Picasso.get().load(lyrics).into(lyricsImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    // Jika berhasil memuat gambar
                    lyricsImageView.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    // Jika gagal memuat gambar
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to load lyrics image.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "No lyrics image available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menambahkan lagu ke "favorites" di Firestore
    private fun addToFavorites(title: String, artist: String, imageUrl: String, lyricsUrl: String) {
        val favoriteSong = hashMapOf(
            "title" to title,
            "artist" to artist,
            "imageUrl" to imageUrl,
            "lyrics" to lyricsUrl
        )

        firestore.collection("favorites")
            .document(title) // Menggunakan title lagu sebagai ID dokumen
            .set(favoriteSong)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$title has been added to favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error adding to favorites: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
