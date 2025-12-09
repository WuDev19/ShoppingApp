package com.example.shoppingapp.ui.view.fragment.bottom_nav

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.FragmentProfileBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.auth.LoginActivity
import com.example.shoppingapp.ui.viewmodel.bottom_nav.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUserProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> Unit
                        is StateListener.Error -> {
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }

                        is StateListener.Success -> {
                            val user = it.result
                            binding.apply {
                                Glide.with(this@ProfileFragment)
                                    .load(user.avatar)
                                    .placeholder(R.drawable.place_holder)
                                    .error(R.drawable.icon_avatar)
                                    .into(imgAvt)
                                tvEmail.text = user.email
                                tvUsername.text = user.ten
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.shimmer.visibility = View.VISIBLE
                        }

                        is StateListener.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.shimmer.visibility = View.GONE
                            ToastUtils.showToast(requireContext(), "Something went wrong")
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e.toString()}")
                        }

                        is StateListener.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.shimmer.visibility = View.GONE
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            controller.navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.linearCart.setOnClickListener {
            val bundle = bundleOf(
                StringConstants.CURRENT_USER_ID to viewModel.getCurrentUserId()
            )
            controller.navigate(R.id.action_profileFragment_to_cartFragment3, bundle)
        }

        binding.btnLogOut.setOnClickListener {
            viewModel.logout()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}