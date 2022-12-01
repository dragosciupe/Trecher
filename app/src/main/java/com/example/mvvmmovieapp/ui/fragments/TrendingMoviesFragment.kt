package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentTrendingMoviesBinding
import com.example.mvvmmovieapp.ui.adapters.MovieAdapter
import com.example.mvvmmovieapp.util.Resource
import com.example.mvvmmovieapp.viewmodels.TrendingMoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingMoviesFragment: Fragment() {
    private var _binding: FragmentTrendingMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieAdapter: MovieAdapter
    private val viewModel: TrendingMoviesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrendingMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.getTrendingMovies()

        movieAdapter.setOnClickFunction {
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(R.id.action_trendingMoviesFragment_to_singleMovieFragment, bundle)
        }

        viewModel.trendingMoviesResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        movieAdapter.differ.submitList(movieResponse.results)
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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(requireContext())
        binding.rvTrendingMovies.adapter = movieAdapter
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}