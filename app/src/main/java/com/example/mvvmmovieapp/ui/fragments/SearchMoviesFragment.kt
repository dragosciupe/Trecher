package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.apidata.search.SearchItem
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.databinding.FragmentSearchMoviesBinding
import com.example.mvvmmovieapp.ui.adapters.SearchAdapter
import com.example.mvvmmovieapp.util.Resource
import com.example.mvvmmovieapp.viewmodels.SearchMoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchMoviesFragment: Fragment() {
    private var _binding: FragmentSearchMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchMoviesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        searchAdapter.setOnClickFunction { searchItem ->
            val movieItem = toMovieItem(searchItem)
            val bundle = Bundle().apply {
                putSerializable("movie", movieItem)
            }
            findNavController().navigate(R.id.action_searchMoviesFragment_to_singleMovieFragment, bundle)
        }
        //Handling the movie search
        var job: Job? = null
        binding.movieSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(movieName: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    if(!movieName.isNullOrEmpty()) {
                        viewModel.searchMovie(movieName)
                        Log.d("SearchMovie", "Searching for $movieName")
                    }
                }
                return true
            }
        })

        viewModel.movieSearchResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { searchResponse ->
                        searchAdapter.differ.submitList(searchResponse.results)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
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
        searchAdapter = SearchAdapter(requireContext())
        binding.rvSearchMovies.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun toMovieItem(searchItem: SearchItem): MovieItem {
        return MovieItem(
            searchItem.adult,
            searchItem.backdrop_path,
            searchItem.genre_ids,
            searchItem.id,
            "",
            searchItem.original_language,
            searchItem.original_title,
            searchItem.overview,
            searchItem.popularity,
            searchItem.poster_path,
            searchItem.release_date,
            searchItem.original_title,
            searchItem.video,
            searchItem.vote_average,
            searchItem.vote_count
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}