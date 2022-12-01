package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentSingleMovieBinding
import com.example.mvvmmovieapp.ui.adapters.CastAdapter
import com.example.mvvmmovieapp.ui.adapters.GenreAdapter
import com.example.mvvmmovieapp.util.Constants.MOVIE_IMAGE_URL
import com.example.mvvmmovieapp.util.Resource
import com.example.mvvmmovieapp.viewmodels.SingleMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_single_movie.*

@AndroidEntryPoint
class SingleMovieFragment: Fragment() {
    private var _binding: FragmentSingleMovieBinding? = null
    private val binding get() = _binding!!
    private val args: SingleMovieFragmentArgs by navArgs()
    private lateinit var castAdapter: CastAdapter
    private lateinit var genreAdapter: GenreAdapter
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

        setupRecyclerView()

        val movie = args.movie

        viewModel.getMovieDetails(movie.id)
        viewModel.getMovieCredits(movie.id)

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
                viewModel.saveMovie(movie)
                Toast.makeText(requireContext(), "Added to watchlist", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.movieCastResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
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
        })

        viewModel.movieDetailsResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
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
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
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
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}