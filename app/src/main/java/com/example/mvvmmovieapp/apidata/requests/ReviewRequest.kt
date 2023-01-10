package com.example.mvvmmovieapp.apidata.requests

data class ReviewRequest(
    val accountUsername: String,
    val movieId: Int,
    val reviewRating: Double,
    val reviewMessage: String
)
