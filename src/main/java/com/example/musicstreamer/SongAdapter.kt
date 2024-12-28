package com.example.musicstreamer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SongAdapter(private val onClick: (Song) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    fun submitList(newSongs: List<Song>) {
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, onClick)
    }

    override fun getItemCount(): Int = songs.size

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val artistTextView: TextView = itemView.findViewById(R.id.artistTextView)

        fun bind(song: Song, onClick: (Song) -> Unit) {
            Picasso.get().load(song.imageUrl).into(coverImageView)
            titleTextView.text = song.title
            artistTextView.text = song.artist
            itemView.setOnClickListener { onClick(song) }
        }
    }
}
