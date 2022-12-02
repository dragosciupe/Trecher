package com.example.mvvmmovieapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvvmmovieapp.R
import com.example.mvvmmovieapp.databinding.FragmentProfileBinding
import com.example.mvvmmovieapp.ui.LoginActivity
import com.example.mvvmmovieapp.viewmodels.ProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        computeUserText()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.logoutLayout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
                navigateToLoginPage()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun computeUserText() = lifecycleScope.launch {
        val user = viewModel.getCurUser()
        binding.curUserText.setText("You are logged in as $user")
    }

    private fun navigateToLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}