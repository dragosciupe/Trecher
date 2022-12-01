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
import com.example.mvvmmovieapp.util.Constants.MOVIE_IMAGE_URL
import kotlinx.android.synthetic.main.cast_item_view.view.*

class CastAdapter: RecyclerView.Adapter<CastAdapter.CastViewHolder>() {
    inner class CastViewHolder(view: View): RecyclerView.ViewHolder(view)

    private val differCallback = object: DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val currentCast = differ.currentList[position]
        holder.itemView.apply {
            if(currentCast.profile_path != null) {
                val castPhotoUrl = MOVIE_IMAGE_URL + currentCast.profile_path
                Glide.with(this).load(castPhotoUrl).into(cast_member_photo)
            } else {
                cast_member_photo.setImageResource(R.drawable.no_photo)
            }
            cast_member_name.text = currentCast.name
            cast_member_character_name.text = currentCast.character
        }
    }

    override fun getItemCount() = differ.currentList.size
}