package com.example.mvvmmovieapp.apidata.reponses

data class ReviewResponse(
    val username: String,
    val movieId: Int,
    val rating: Double,
    val message: String,
    val timestamp: Long
)
