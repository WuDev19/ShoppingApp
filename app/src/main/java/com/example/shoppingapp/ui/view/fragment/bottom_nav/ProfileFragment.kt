package com.example.shoppingapp.ui.view.fragment.bottom_nav

import android.app.AlertDialog
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.CustomDialogImageAvatarCircleBinding
import com.example.shoppingapp.databinding.FragmentProfileBinding
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.activity.auth.LoginActivity
import com.example.shoppingapp.ui.viewmodel.bottom_nav.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(
            R.id.bottom_navigation_view
        )
    }

    private lateinit var user: BaseUser

    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
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

        viewModel.getUserProfile()

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
                            user = it.result
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

        binding.apply {

            btnEdit.setOnClickListener {
                controller.navigate(R.id.action_profileFragment_to_editProfileFragment)
            }

            linearCart.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.CURRENT_USER_ID to viewModel.getCurrentUserId()
                )
                controller.navigate(R.id.action_profileFragment_to_cartFragment3, bundle)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                ViewPagerUtils.disableViewPager(viewPager2)
            }

            btnLogOut.setOnClickListener {
                viewModel.logout()
            }

            imgAvt.setOnClickListener {
                val dialogView = CustomDialogImageAvatarCircleBinding.inflate(layoutInflater)
                Glide.with(requireContext())
                    .load(user.avatar)
                    .placeholder(R.drawable.place_holder)
                    .fitCenter()
                    .error(R.drawable.icon_avatar)
                    .into(dialogView.imgAnh)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView.root)
                    .create()
                dialog.show()
                dialog.window?.apply {
                    setGravity(Gravity.CENTER)
                    setLayout(
                        resources.displayMetrics.widthPixels * 80 / 100,
                        resources.displayMetrics.heightPixels * 50 / 100
                    )
                    setBackgroundDrawableResource(R.drawable.custom_dialog)
                }
            }

            linearAddress.setOnClickListener {
                controller.navigate(R.id.action_profileFragment_to_addressFragment)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                ViewPagerUtils.disableViewPager(viewPager2)
            }

            linearRegisterSeller.setOnClickListener {
                controller.navigate(R.id.action_profileFragment_to_registerSellerFragment)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                ViewPagerUtils.disableViewPager(viewPager2)
            }

            cardSeller.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToDashBoardSellerFragment(
                        viewModel.getCurrentUserId()
                    )
                controller.navigate(action)
                BottomNavigationUtils.hideBottomNavigation(bottomNavigationView)
                ViewPagerUtils.disableViewPager(viewPager2)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}