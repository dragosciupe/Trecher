package com.example.mvvmmovieapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.apidata.cast.Cast
import com.example.mvvmmovieapp.apidata.moviedetails.Genre
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.util.Constants.MOVIE_IMAGE_URL
import kotlinx.android.synthetic.main.cast_item_view.view.*
import kotlinx.android.synthetic.main.genre_item_view.view.*

class GenreAdapter: RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {
    inner class GenreViewHolder(view: View): RecyclerView.ViewHolder(view)

        private val differCallback = object: DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreAdapter.GenreViewHolder {
        return GenreViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.genre_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GenreAdapter.GenreViewHolder, position: Int) {
        val currentGenre = differ.currentList[position]
        holder.itemView.tv_genre.text = currentGenre.name
    }

    override fun getItemCount() = differ.currentList.size
}