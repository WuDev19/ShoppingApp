package com.example.shoppingapp.ui.view.fragment.seller

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.databinding.CustomDialogImageAvatarCircleBinding
import com.example.shoppingapp.databinding.FragmentRegisterSellerBinding
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterSellerFragment : Fragment() {
    private var _binding: FragmentRegisterSellerBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }
    private var uriAnh: Uri? = null
    private val resultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uriAnh = uri
                Glide.with(requireContext())
                    .load(uri)
                    .error(R.drawable.icon_avatar)
                    .into(binding.imgAvt)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            btnContinue.setOnClickListener {
                val shopName = edtShopName.text.toString().trim()
                val shopAddress = edtAddressShop.text.toString().trim()
                val action =
                    RegisterSellerFragmentDirections.actionRegisterSellerFragmentToSendOtpFragment(
                        uriAnh,
                        shopName,
                        shopAddress
                    )
                controller.navigate(action)
            }

            imgMap.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.ADDRESS_DETAIL to edtAddressShop.text.toString()
                )
                controller.navigate(R.id.action_registerSellerFragment_to_mapFragment2, bundle)
            }

            linearAddAvt.setOnClickListener {
                resultLauncher.launch("image/*")
            }

            imgAvt.setOnClickListener {
                val viewDialog = CustomDialogImageAvatarCircleBinding.inflate(layoutInflater)
                Glide.with(requireContext())
                    .load(uriAnh)
                    .fitCenter()
                    .override(200, 200)
                    .error(R.drawable.icon_avatar)
                    .into(viewDialog.imgAnh)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(viewDialog.root)
                    .create()
                dialog.show()
                dialog.window?.apply {
                    setGravity(Gravity.CENTER)
                    setLayout(
                        resources.displayMetrics.widthPixels * 80 / 100,
                        resources.displayMetrics.heightPixels * 50 / 100,
                    )
                    setBackgroundDrawableResource(R.drawable.custom_dialog)
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(
                        StringConstants.ADDRESS_DETAIL,
                        ""
                    )?.collectLatest {
                        edtAddressShop.setText(it)
                    }
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}