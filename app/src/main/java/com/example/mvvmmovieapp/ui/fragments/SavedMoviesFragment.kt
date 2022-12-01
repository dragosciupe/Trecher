package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentSavedMoviesBinding
import com.example.mvvmmovieapp.ui.SwipeItemCallback
import com.example.mvvmmovieapp.ui.adapters.SavedMovieAdapter
import com.example.mvvmmovieapp.viewmodels.SavedMoviesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedMoviesFragment : Fragment() {
    private var _binding: FragmentSavedMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedMovieAdapter: SavedMovieAdapter
    private val viewModel: SavedMoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()
        viewModel.getFavoriteMovies()

        savedMovieAdapter.setOnClickFunction {
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_savedMoviesFragment_to_singleMovieFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object: SwipeItemCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedMovie = savedMovieAdapter.differ.currentList[position]
                viewModel.deleteMovie(swipedMovie, savedMovieAdapter.differ.currentList)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback)
            .attachToRecyclerView(binding.rvWatchlist)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        savedMovieAdapter = SavedMovieAdapter(requireContext())
        binding.rvWatchlist.apply {
            adapter = savedMovieAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesFlow.collect { favoriteMovies ->
                    savedMovieAdapter.differ.submitList(favoriteMovies)
                    toggleLoadingBar(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedMoviesEvent.collect { savedMoviesEvent ->
                    when(savedMoviesEvent) {
                        is SavedMoviesViewModel.SavedMoviesEvent.SavedMoviesError -> {
                            showSnackBar(savedMoviesEvent.message)
                            toggleLoadingBar(false)
                        }
                        is SavedMoviesViewModel.SavedMoviesEvent.DeleteMovieResult -> {
                            showSnackBar(savedMoviesEvent.message)
                            toggleLoadingBar(false)
                        }
                        is SavedMoviesViewModel.SavedMoviesEvent.SavedMoviesLoading -> {
                            toggleLoadingBar(true)
                        }
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun toggleLoadingBar(state: Boolean) {
        binding.progressBar.isVisible = state;
    }
}