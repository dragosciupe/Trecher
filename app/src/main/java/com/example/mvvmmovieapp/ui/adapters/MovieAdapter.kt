package com.example.mvvmmovieapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.util.Constants.MOVIE_IMAGE_URL
import kotlinx.android.synthetic.main.movie_item_view.view.*

class MovieAdapter(val context: Context): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val genreMap = hashMapOf(28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy", 80 to "Crime",
        99 to "Documentary", 18 to "Drama", 10751 to "Family", 14 to "Fantasy", 36 to "History", 27 to "Horror",
        10402 to "Music", 9648 to "Mystery", 10749 to "Romance", 878 to "Science Fiction", 10770 to "TV Movie",
        53 to "Thriller", 10752 to "War", 37 to "Western")

    inner class MovieViewHolder(view: View): RecyclerView.ViewHolder(view)

    private val diffCallback = object: DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item_view, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]

        holder.itemView.apply {
            val movieImgUrl = MOVIE_IMAGE_URL + currentMovie.poster_path
            Glide.with(this).load(movieImgUrl).into(movie_image)
            movie_title.text = currentMovie.title
            movie_rating_bar.rating = (currentMovie.vote_average / 2).toFloat()
            movie_rating_count.text = currentMovie.vote_count.toString()
            movie_genre.text = "Genre: " + genreMap.get(currentMovie.genre_ids.first())
            movie_release_date.text = context.resources.getString(R.string.release_date)
                .format(currentMovie.release_date)
            setOnClickListener {
                onClick?.let { openMovieScreen ->
                    openMovieScreen(currentMovie)
                }
            }
        }
    }

    private var onClick:((MovieItem) -> Unit)? = null

    fun setOnClickFunction(listener: (MovieItem) -> Unit) {
        onClick = listener
    }

}