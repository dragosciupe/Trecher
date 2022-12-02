package com.example.mvvmmovieapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentLoginBinding
import com.example.mvvmmovieapp.databinding.FragmentRegisterBinding
import com.example.mvvmmovieapp.util.snackbar
import com.example.mvvmmovieapp.viewmodels.LoginFragmentViewModel
import com.example.mvvmmovieapp.viewmodels.RegisterFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        subscribeToObservers()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.registerAccount(
                binding.etRegisterUsername.text.toString(),
                binding.etRegisterPassword.text.toString(),
                binding.etRegisterPasswordRepeat.text.toString()
            )
        }
    }

    private fun toggleProgressBar(state: Boolean) {
        binding.progressBar.isVisible = state
    }

    private fun clearRegisterForm() {
        binding.etRegisterUsername.text.clear()
        binding.etRegisterPassword.text.clear()
        binding.etRegisterPasswordRepeat.text.clear()
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerEvents.collect { registerEvent ->
                    when(registerEvent) {
                        is RegisterFragmentViewModel.RegisterEvent.RegisterSuccess -> {
                            snackbar(registerEvent.message)
                            toggleProgressBar(false)
                            clearRegisterForm()
                        }
                        is RegisterFragmentViewModel.RegisterEvent.RegisterError -> {
                            snackbar(registerEvent.message)
                            toggleProgressBar(false)
                        }
                        is RegisterFragmentViewModel.RegisterEvent.RegisterLoading -> {
                            toggleProgressBar(true)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}