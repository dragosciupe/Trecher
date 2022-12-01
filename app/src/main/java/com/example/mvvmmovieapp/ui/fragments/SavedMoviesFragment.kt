package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
                viewModel.deleteMovie(swipedMovie)

                Snackbar.make(view, "Undo", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveMovie(swipedMovie)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback)
            .attachToRecyclerView(binding.rvWatchlist)

        viewModel.getSavedMovies().observe(viewLifecycleOwner, { watchlistMovies ->
            savedMovieAdapter.differ.submitList(watchlistMovies)
        })
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
}