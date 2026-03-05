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
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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
    private var lastProvince: ProvinceGhn? = null
    private var lastDistrict: DistrictGhn? = null
    private var lastWard: WardGhn? = null

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
                    viewModel.addressState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> {
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
                                Log.e(StringConstants.DEBUG, "onViewCreated: failed ${it.e?.message}")
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
                                Log.e(StringConstants.DEBUG, "onViewCreated: failed ${it.e?.message}")
                            }

                            is StateListener.Success<List<DistrictGhn>> -> {
                                Log.e(StringConstants.DEBUG, "onViewCreated: ${it.result}")
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
                                Log.e(StringConstants.DEBUG, "onViewCreated: failed ${it.e?.message}")
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

            addressItem?.let { item ->
                val addresses = item.addressName.split(", ")
                lastProvince = ProvinceGhn(item.provinceId, addresses[3])
                lastDistrict = DistrictGhn(item.districtId, item.provinceId, addresses[2])
                lastWard = WardGhn(item.wardCode, item.districtId, addresses[1])
                tvUsername.setText(item.name)
                tvSdt.setText(item.sdt)
                tvDetailAddress.setText(addresses[0])
                tvSelectWard.setText(addresses[1])
                tvSelectDistrict.setText(addresses[2])
                tvSelectProvince.setText(addresses[3])
                swDefault.isChecked = item.defaultAddress
            }

            tvSelectProvince.apply {
                setAdapter(selectProvinceAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val item = adapter.getItem(position)
                        if (item is ProvinceGhn) {
                            if (lastProvince?.provinceName == item.provinceName) {
                                return@OnItemClickListener
                            }
                            lastProvince = item
                            if (tvSelectDistrict.text.isNotEmpty()) tvSelectDistrict.setText("")
                            if (tvSelectWard.text.isNotEmpty()) tvSelectWard.setText("")
                            if (tvDetailAddress.text.toString()
                                    .isNotEmpty()
                            ) tvDetailAddress.setText("")
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${item.provinceID}")
                            viewModel.getDistrictOfProvince(item.provinceID)
                        }
                    }
            }

            tvSelectDistrict.apply {
                setAdapter(selectDistrictAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val item = adapter.getItem(position)
                        if (item is DistrictGhn) {
                            if (lastDistrict?.districtName == item.districtName) {
                                return@OnItemClickListener
                            }
                            lastDistrict = item
                            if (tvSelectWard.text.isNotEmpty()) tvSelectWard.setText("")
                            if (tvDetailAddress.text.toString()
                                    .isNotEmpty()
                            ) tvDetailAddress.setText("")
                            viewModel.getWardOfDistrict(item.districtID)
                        }
                    }
            }

            tvSelectWard.apply {
                setAdapter(selectWardAdapter)
                onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val item = adapter.getItem(position)
                        if (item is WardGhn) {
                            if (lastWard?.wardName == item.wardName) {
                                return@OnItemClickListener
                            }
                            lastWard = item
                            if (tvDetailAddress.text.toString()
                                    .isNotEmpty()
                            ) tvDetailAddress.setText("")
                        }
                    }
            }

//            imgMap.setOnClickListener {
//                val bundle = bundleOf(
//                    StringConstants.ADDRESS_DETAIL to tvDetailAddress.text.toString()
//                )
//                CheckNavigation.navigateTo(
//                    controller,
//                    R.id.action_editAddressFragment2_to_mapFragment3,
//                    R.id.action_editAddressFragment3_to_mapFragment2,
//                    bundle,
//                    R.id.action_editAddressFragment_to_mapFragment
//                )
//            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            btnSave.setOnClickListener {
                val address = tvDetailAddress.text.toString()
                val default = swDefault.isChecked
                val username = tvUsername.text.toString()
                val phone = tvSdt.text.toString()
                if (addressItem == null) {
                    addAddress(address, default, username, phone)
                } else {
                    updateAddress(address, default, username, phone)
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

    private fun addAddress(
        address: String,
        default: Boolean,
        username: String,
        phone: String
    ) {
        if (lastProvince == null ||
            lastDistrict == null ||
            lastWard == null ||
            address.isEmpty() ||
            username.isEmpty() ||
            phone.isEmpty()
        ) {
            ToastUtils.showToast(requireContext(), "Please select full information")
            return
        }
        val addressName = buildString {
            append(address)
            append(", ")
            append(lastWard!!.wardName)
            append(", ")
            append(lastDistrict!!.districtName)
            append(", ")
            append(lastProvince!!.provinceName)
        }
        viewModel.addAddress(
            UserAddressRequest(
                addressName,
                lastProvince!!.provinceID,
                lastDistrict!!.districtID,
                lastWard!!.wardCode,
                default,
                username,
                phone,
                userId
            )
        )
    }

    private fun updateAddress(
        address: String,
        default: Boolean,
        username: String,
        phone: String
    ) {
        val detailAddress = addressItem?.addressName?.split(", ")[0]
        if (address == detailAddress &&
            default == addressItem?.defaultAddress &&
            username == addressItem?.name &&
            phone == addressItem?.sdt
        ) {
            ToastUtils.showToast(requireContext(), StringConstants.CHANGE_ADDRESS)
        } else {
            val addressName = buildString {
                append(address)
                append(", ")
                append(lastWard!!.wardName)
                append(", ")
                append(lastDistrict!!.districtName)
                append(", ")
                append(lastProvince!!.provinceName)
            }
            viewModel.updateAddress(
                addressItem!!.idAddress, UserAddressRequest(
                    addressName,
                    lastProvince!!.provinceID,
                    lastDistrict!!.districtID,
                    lastWard!!.wardCode,
                    default,
                    username,
                    phone,
                    userId
                )
            )
        }
    }
}