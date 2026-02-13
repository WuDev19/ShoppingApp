package com.example.shoppingapp.ui.view.fragment.seller

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.CustomDialogImageAvatarCircleBinding
import com.example.shoppingapp.databinding.FragmentSellerSettingBinding
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.SettingSellerAdapter
import com.example.shoppingapp.ui.viewmodel.seller.SellerSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerSettingFragment : Fragment() {

    private val viewModel: SellerSettingViewModel by viewModels()
    private var _binding: FragmentSellerSettingBinding? = null
    private val binding
        get() = _binding!!
    private var uriUpload: Uri? = null
    private val controller by lazy { findNavController() }
    private val galleryResult: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uriUpload = it
            settingSellerAdapter.setAvatar(uri = uri)
        }
    }
    private val settingSellerAdapter by lazy {
        SettingSellerAdapter(
            { newName ->
                viewModel.editProfile(
                    mapOf(
                        "newName" to newName
                    )
                )
            },
            { newAddress ->
                viewModel.editProfile(
                    mapOf(
                        "newAddress" to newAddress
                    )
                )
            },
            { newPhone ->
                viewModel.editProfile(
                    mapOf(
                        "newPhone" to newPhone
                    )
                )
            },
            {
                uriUpload?.let {
                    viewModel.uploadAvatarBeforeUpdateProfile(it)
                }
            },
            {
                galleryResult.launch("image/*")
            },
            { url ->
                showPreviewAvatar(url)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getSellerDetail.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<SellerInStore> -> {
                                val sellerInfo = it.result
                                Log.e(
                                    StringConstants.DEBUG,
                                    "onViewCreated: 2 ${sellerInfo.avtShop}"
                                )
                                settingSellerAdapter.setAvatar(url = sellerInfo.avtShop)
                            }
                        }
                    }
                }
                launch {
                    viewModel.editState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
                                binding.apply {
                                    shimmer.isVisible = true
                                    progressBar.isVisible = true
                                }
                            }

                            is StateListener.Error -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                                binding.apply {
                                    shimmer.isVisible = false
                                    progressBar.isVisible = false
                                }
                                ToastUtils.showToast(
                                    requireContext(),
                                    StringConstants.UPDATE_FAILED
                                )
                            }

                            is StateListener.Success<Unit> -> {
                                binding.apply {
                                    shimmer.isVisible = false
                                    progressBar.isVisible = false
                                }
                                settingSellerAdapter.refresh()
                                ToastUtils.showToast(
                                    requireContext(),
                                    StringConstants.UPDATE_SUCCESS
                                )
                            }
                        }
                    }
                }
            }
        }

        binding.apply {

            recyclerViewSetting.apply {
                adapter = settingSellerAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPreviewAvatar(url: String) {
        val view = CustomDialogImageAvatarCircleBinding.inflate(layoutInflater)
        loadAvatar(view.imgAnh, url)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view.root)
            .create()
        dialog.show()
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(R.drawable.custom_dialog)
            setLayout(
                resources.displayMetrics.widthPixels * 80 / 100,
                resources.displayMetrics.heightPixels * 80 / 100
            )
        }
    }

    private fun loadAvatar(imageView: ImageView, url: String? = null, uri: Uri? = null) {
        when {
            url != null -> {
                Glide.with(requireContext())
                    .load(url)
                    .error(R.drawable.icon_avatar)
                    .into(imageView)
            }

            uri != null -> {
                Glide.with(requireContext())
                    .load(uri)
                    .error(R.drawable.icon_avatar)
                    .into(imageView)
            }
        }
    }

}