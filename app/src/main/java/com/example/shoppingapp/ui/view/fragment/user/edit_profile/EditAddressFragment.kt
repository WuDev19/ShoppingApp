package com.example.shoppingapp.ui.view.fragment.user.edit_profile

import SelectAddressAdapter
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.user.edit_profile.EditAddressViewModel
import com.example.shoppingapp.utils.CheckNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class EditAddressFragment : Fragment() {

    private val viewModel: EditAddressViewModel by viewModels()
    private var _binding: FragmentEditAddressBinding? = null
    private val binding get() = _binding!!
    private val controller by lazy { findNavController() }

    @Inject
    @Named("userId")
    lateinit var userId: String
    private val addressItem by lazy {
        //dữ liệu từ address fragment
        arguments?.getParcelable(StringConstants.ADDRESS_ITEM, AddressItem::class.java)
    }
    private lateinit var selectProvinceAdapter: SelectAddressAdapter
    private lateinit var selectDistrictAdapter: SelectAddressAdapter
    private lateinit var selectWardAdapter: SelectAddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectProvinceAdapter = SelectAddressAdapter(requireContext(), emptyList())
        selectDistrictAdapter = SelectAddressAdapter(requireContext(), emptyList())
        selectWardAdapter = SelectAddressAdapter(requireContext(), emptyList())
        viewModel.getProvinces()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(
                        StringConstants.ADDRESS_DETAIL,
                        addressItem?.addressName
                    )
                        ?.collectLatest {
                            binding.tvDetailAddress.setText(it)
                        }
                }
                launch {
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
                launch {
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
                launch {
                    viewModel.provincesState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<List<ProvinceGhn>> -> {
                                selectProvinceAdapter.setData(it.result)
                            }
                        }
                    }
                }
                launch {
                    viewModel.districtState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<List<DistrictGhn>> -> {
                                selectDistrictAdapter.setData(it.result)
                            }
                        }
                    }
                }
                launch {
                    viewModel.wardState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            }

                            is StateListener.Success<List<WardGhn>> -> {
                                selectWardAdapter.setData(it.result)
                            }
                        }
                    }
                }
            }
        }

        binding.apply {

            tvSelectProvince.apply {
                setAdapter(selectProvinceAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { parentView, currentView, position, viewId ->
                        tvSelectDistrict.isEnabled = true
                        val item = adapter.getItem(position)
                        if (item is ProvinceGhn) {
                            viewModel.getDistrictOfProvince(item.provinceID)
                        }
                    }
            }

            tvSelectDistrict.apply {
                setAdapter(selectDistrictAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { parentView, currentView, position, viewId ->
                        tvSelectWard.isEnabled = true
                        val item = adapter.getItem(position)
                        if (item is DistrictGhn) {
                            viewModel.getWardOfDistrict(item.districtID)
                        }
                    }
            }

            tvSelectWard.apply {
                setAdapter(selectWardAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { parentView, currentView, position, viewId ->
                        val item = adapter.getItem(position)
                        if (item is WardGhn) {
                            Log.e(StringConstants.DEBUG, "onViewCreated: $item")
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
                CheckNavigation.navigateTo(
                    controller,
                    R.id.action_editAddressFragment2_to_mapFragment3,
                    R.id.action_editAddressFragment3_to_mapFragment2,
                    bundle,
                    R.id.action_editAddressFragment_to_mapFragment
                )
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
                            phone,
                            userId
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
                                phone,
                                userId
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}