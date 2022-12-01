package com.example.mvvmmovieapp.ui.adapters

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
import kotlinx.android.synthetic.main.movie_item_view.view.movie_image
import kotlinx.android.synthetic.main.movie_item_view.view.movie_rating_bar
import kotlinx.android.synthetic.main.movie_item_view.view.movie_rating_count
import kotlinx.android.synthetic.main.movie_item_view.view.movie_release_date
import kotlinx.android.synthetic.main.movie_item_view.view.movie_title

class MovieAdapter(val context: Context): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

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

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]

        holder.itemView.apply {
            val movieImgUrl = MOVIE_IMAGE_URL + currentMovie.poster_path
            Glide.with(this).load(movieImgUrl).into(movie_image)
            movie_title.text = currentMovie.title
            movie_rating_bar.rating = (currentMovie.vote_average / 2).toFloat()
            movie_rating_count.text = currentMovie.vote_count.toString()
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