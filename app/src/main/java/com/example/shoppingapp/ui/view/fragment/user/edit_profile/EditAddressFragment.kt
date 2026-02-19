package com.example.shoppingapp.ui.view.fragment.user.edit_profile

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.databinding.FragmentEditAddressBinding
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.user.edit_profile.EditAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class EditAddressFragment : Fragment() {

    private val viewModel: EditAddressViewModel by viewModels()
    private var _binding: FragmentEditAddressBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }

    private val addressItem by lazy {
        //dữ liệu từ address fragment
        arguments?.getParcelable(StringConstants.ADDRESS_ITEM, AddressItem::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(
                        StringConstants.ADDRESS_DETAIL,
                        addressItem?.addressName
                    )
                        ?.collectLatest {
                            tvDetailAddress.setText(it)
                        }
                }
            }

            addressItem?.let { item ->
                tvUsername.setText(item.name)
                tvSdt.setText(item.sdt)
                tvDetailAddress.setText(item.addressName)
                swDefault.isChecked = item.defaultAddress
            }

            imgMap.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.ADDRESS_DETAIL to tvDetailAddress.text.toString()
                )
                controller.navigate(R.id.action_editAddressFragment_to_mapFragment, bundle)
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            btnSave.setOnClickListener {
                val address = tvDetailAddress.text.toString()
                val default = swDefault.isChecked
                val username = tvUsername.text.toString()
                val phone = tvSdt.text.toString()
                if (addressItem == null) {
                    viewModel.addAddress(
                        UserAddressRequest(
                            address,
                            default,
                            username,
                            phone
                        )
                    )
                } else {
                    if (address == addressItem?.addressName &&
                        default == addressItem?.defaultAddress &&
                        username == addressItem?.name &&
                        phone == addressItem?.sdt
                    ) {
                        ToastUtils.showToast(requireContext(), StringConstants.CHANGE_ADDRESS)
                    } else {
                        viewModel.updateAddress(
                            addressItem!!.idAddress, UserAddressRequest(
                                address,
                                default,
                                username,
                                phone
                            )
                        )
                    }
                }
            }

            btnDelete.setOnClickListener {
                addressItem?.let {
                    viewModel.deleteUserAddress(it.maND, it.idAddress)
                }
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addressState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> {
                            Log.e(StringConstants.DEBUG, "onViewCreated: loading")
                            binding.progressBar.isVisible = true
                        }

                        is StateListener.Error -> {
                            if (it.e !is IllegalArgumentException) {
                                delay(2000)
                                binding.progressBar.isVisible = false
                            }
                            ToastUtils.showToast(requireContext(), it.e?.message.toString())
                        }

                        is StateListener.Success<Unit> -> {
                            delay(2000)
                            binding.progressBar.isVisible = false
                            ToastUtils.showToast(
                                requireContext(),
                                StringConstants.SAVE_SUCCESSFULLY
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.isVisible = true
                        is StateListener.Error -> {
                            delay(2000)
                            ToastUtils.showToast(requireContext(), it.e?.message.toString())
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            binding.progressBar.isVisible = false
                        }

                        is StateListener.Success<Int> -> {
                            delay(2000)
                            binding.progressBar.isVisible = false
                            controller.popBackStack()
                        }
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