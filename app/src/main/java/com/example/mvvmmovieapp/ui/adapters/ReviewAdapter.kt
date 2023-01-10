package com.example.mvvmmovieapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.apidata.reponses.ReviewResponse
import kotlinx.android.synthetic.main.review_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter(private val loggedInUsername: String): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View): RecyclerView.ViewHolder(view)

    val diffCallback = object: DiffUtil.ItemCallback<ReviewResponse>() {
        override fun areItemsTheSame(oldItem: ReviewResponse, newItem: ReviewResponse): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: ReviewResponse, newItem: ReviewResponse): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.review_item_view, parent, false))
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentReview = differ.currentList[position]

        holder.itemView.apply {
            tvName.text = if(currentReview.username == loggedInUsername) "You" else currentReview.username;
            ratingBar.rating = currentReview.rating.toFloat()
            tvRatingScore.text = String.format("(%.1f)", currentReview.rating)
            tvRatingText.text = currentReview.message

            val dateFormatPattern = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val date = Date(currentReview.timestamp)

            tvReviewData.text = dateFormatPattern.format(date)

        }
    }
}