package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentSingleMovieBinding
import com.example.mvvmmovieapp.ui.adapters.CastAdapter
import com.example.mvvmmovieapp.ui.adapters.GenreAdapter
import com.example.mvvmmovieapp.ui.adapters.ReviewAdapter
import com.example.mvvmmovieapp.util.Constants.MOVIE_IMAGE_URL
import com.example.mvvmmovieapp.util.Resource
import com.example.mvvmmovieapp.viewmodels.SingleMovieViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_single_movie.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SingleMovieFragment: Fragment() {
    private var _binding: FragmentSingleMovieBinding? = null
    private val binding get() = _binding!!
    private val args: SingleMovieFragmentArgs by navArgs()
    private lateinit var castAdapter: CastAdapter
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var reviewAdapter: ReviewAdapter
    private val viewModel: SingleMovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        val movie = args.movie

        subscribeToObservers()
        viewModel.getMovieDetails(movie.id)
        viewModel.getMovieCredits(movie.id)
        viewModel.getAllMovieReviews(movie.id)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = movie.title
        }

        binding.apply {
            if(movie.poster_path != null) {
                val posterImgUrl = MOVIE_IMAGE_URL + movie.poster_path
                Glide.with(this@SingleMovieFragment).load(posterImgUrl).into(movie_overview_image)
            } else {
                movie_overview_image.setImageResource(R.drawable.nophoto)
            }

            if(movie.backdrop_path != null) {
                val backdropImgUrl = MOVIE_IMAGE_URL + movie.backdrop_path
                Glide.with(this@SingleMovieFragment).load(backdropImgUrl).into(movie_image)
            } else {
                movie_image.setImageResource(R.drawable.nophoto)
            }

            movie_title.text = movie.title
            movie_rating_average.text = getString(R.string.vote_average).format(movie.vote_average)
            movie_overview_text.text = movie.overview
            movie_add_to_watchlist.setOnClickListener {
                viewModel.addMovieToFavorites(movie)
            }
            btnAddReview.setOnClickListener {
                viewModel.addMovieReview(
                    etReviewRating.text.toString(),
                    etReview.text.toString(),
                    movie.id
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.singleMovieEvent.collect { singleMoviesEvent ->
                    when(singleMoviesEvent) {
                        is SingleMovieViewModel.SingleMovieEvent.Loading -> {
                            showProgressBar()
                        }
                        is SingleMovieViewModel.SingleMovieEvent.AddMovieResult -> {
                            showSnackBar(singleMoviesEvent.data)
                            hideProgressBar()
                        }
                        is SingleMovieViewModel.SingleMovieEvent.AddReviewResult -> {
                            showSnackBar(singleMoviesEvent.data)
                            hideProgressBar()
                        }
                        is SingleMovieViewModel.SingleMovieEvent.AddReviewSuccess -> {
                            showSnackBar("Review added successfully")
                            binding.etReviewRating.text.clear()
                            binding.etReview.text.clear()
                            hideProgressBar()
                            val newReviewList = reviewAdapter.differ.currentList.toMutableList().apply {
                                add(0, singleMoviesEvent.review)
                            }
                            reviewAdapter.differ.submitList(newReviewList)
                            binding.tvNoReviews.isVisible = false
                        }
                        is SingleMovieViewModel.SingleMovieEvent.GetReviewsError -> {
                            showSnackBar(singleMoviesEvent.message)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reviewsFlow.collect { reviewsEvent ->
                    when(reviewsEvent) {
                        is SingleMovieViewModel.SingleMovieEvent.GetReviewsSuccess -> {
                            Log.d("Reviews", "retrieved in fragment")
                            val reviews = reviewsEvent.reviews
                            if(reviews.isNotEmpty()) {
                                binding.tvNoReviews.isVisible = false
                                reviewAdapter.differ.submitList(reviews)
                            } else {
                                binding.tvNoReviews.isVisible = true
                            }
                        }
                    }
                }
            }
        }

        viewModel.movieCastResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { castResponse ->
                        castAdapter.differ.submitList(castResponse.cast)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                else -> {
                    hideProgressBar()
                }
            }
        }

        viewModel.movieDetailsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { detailsResponse ->
                        binding.movieRuntime.text = getString(R.string.movie_runtime)
                            .format(
                                detailsResponse.runtime / 60,
                                detailsResponse.runtime % 60
                            )
                        genreAdapter.differ.submitList(detailsResponse.genres)
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupRecyclerViews() {
        castAdapter = CastAdapter()
        binding.rvCastMembers.adapter = castAdapter
        binding.rvCastMembers.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )

        genreAdapter = GenreAdapter()
        binding.rvGenres.adapter = genreAdapter
        binding.rvGenres.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )

        reviewAdapter = ReviewAdapter(viewModel.loggedInUsername)
        binding.rvReviews.adapter = reviewAdapter
        binding.rvReviews.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}