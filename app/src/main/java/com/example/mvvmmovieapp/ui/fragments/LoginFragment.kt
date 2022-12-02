package com.example.mvvmmovieapp.ui.fragments

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentLoginBinding
import com.example.mvvmmovieapp.ui.MainActivity
import com.example.mvvmmovieapp.util.snackbar
import com.example.mvvmmovieapp.viewmodels.LoginFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redirectLogin()
        setupClickListeners()
        subscribeToObservers()

    }

    private fun setupClickListeners() {
        binding.btnLoginButton.setOnClickListener {
            viewModel.loginUser(
                binding.etLoginUsername.text.toString(),
                binding.etLoginPassword.text.toString()
            )
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun redirectLogin() {
        lifecycleScope.launch {
            if(viewModel.isUserLoggedIn()) {
                navigateToMainPage()
            }
        }
    }

    private fun navigateToMainPage() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun toggleProgressBar(state: Boolean) {
        binding.progressBar.isVisible = state
    }

    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginEvents.collect { loginEvent ->
                    when(loginEvent) {
                        is LoginFragmentViewModel.LoginEvent.LoginSuccess -> {
                            toggleProgressBar(false)
                            navigateToMainPage()
                        }
                        is LoginFragmentViewModel.LoginEvent.LoginError -> {
                            snackbar(loginEvent.message)
                            toggleProgressBar(false)
                        }
                        is LoginFragmentViewModel.LoginEvent.LoginLoading -> {
                            toggleProgressBar(true)
                        }
                    }
                }
            }
        }
    }
}