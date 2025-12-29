package com.example.shoppingapp.ui.view.fragment.edit_profile

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.databinding.FragmentEditAddressBinding
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.edit_profile.EditAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        arguments?.getParcelable(StringConstants.ADDRESS_ITEM, AddressItem::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(StringConstants.DEBUG, "onCreate: $addressItem")
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

            addressItem?.let { item ->
                tvUsername.setText(item.name)
                tvSdt.setText(item.sdt)
                tvDetailAddress.setText(item.addressName)
                swDefault.isChecked = item.defaultAddress
            }

            imgMap.setOnClickListener {
                controller.navigate(R.id.action_editAddressFragment_to_mapFragment)
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
                        ToastUtils.showToast(requireContext(), "Update your new address")
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

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addressState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.isVisible = true
                        is StateListener.Error -> {
                            ToastUtils.showToast(requireContext(), it.e?.message.toString())
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            binding.progressBar.isVisible = false
                        }
                        is StateListener.Success<*> -> binding.progressBar.isVisible = false
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