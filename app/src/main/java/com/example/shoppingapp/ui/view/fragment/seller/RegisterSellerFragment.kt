package com.example.shoppingapp.ui.view.fragment.seller

import SelectAddressAdapter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.databinding.CustomDialogImageAvatarCircleBinding
import com.example.shoppingapp.databinding.FragmentRegisterSellerBinding
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.seller.RegisterSellerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
@AndroidEntryPoint
class RegisterSellerFragment : Fragment() {
    private val viewModel: RegisterSellerViewModel by viewModels()
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
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(R.id.bottom_navigation_view)
    }
    private val viewPager2: ViewPager2 by lazy {
        requireActivity().findViewById(R.id.view_pager2)
    }
    private lateinit var selectProvinceAdapter: SelectAddressAdapter
    private lateinit var selectDistrictAdapter: SelectAddressAdapter
    private lateinit var selectWardAdapter: SelectAddressAdapter
    private var lastProvince: ProvinceGhn? = null
    private var lastDistrict: DistrictGhn? = null
    private var lastWard: WardGhn? = null

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
        selectProvinceAdapter = SelectAddressAdapter(requireContext(), emptyList())
        selectDistrictAdapter = SelectAddressAdapter(requireContext(), emptyList())
        selectWardAdapter = SelectAddressAdapter(requireContext(), emptyList())
        viewModel.getProvinces()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val item = adapter.getItem(position)
                        if (item is ProvinceGhn) {
                            if (lastProvince?.provinceName == item.provinceName) {
                                return@OnItemClickListener
                            }
                            lastProvince = item
                            if (tvSelectDistrict.text.isNotEmpty()) tvSelectDistrict.setText("")
                            if (tvSelectWard.text.isNotEmpty()) tvSelectWard.setText("")
                            if (edtAddressShop.text.toString()
                                    .isNotEmpty()
                            ) edtAddressShop.setText("")
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
                            if (edtAddressShop.text.toString()
                                    .isNotEmpty()
                            ) edtAddressShop.setText("")
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
                            if (edtAddressShop.text.toString()
                                    .isNotEmpty()
                            ) edtAddressShop.setText("")
                        }
                    }
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
                ViewPagerUtils.enableViewPager(viewPager2, controller)
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
            }

            btnContinue.setOnClickListener {
                val shopName = edtShopName.text.toString().trim()
                val shopAddressDetail = edtAddressShop.text.toString().trim()
                if (shopName.isEmpty() ||
                    shopAddressDetail.isEmpty() ||
                    uriAnh == null ||
                    lastProvince == null ||
                    lastDistrict == null ||
                    lastWard == null) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Please fill all information to register seller"
                    )
                    return@setOnClickListener
                }
                val shopAddress = buildString {
                    append(shopAddressDetail)
                    append(", ")
                    append(lastWard!!.wardName)
                    append(", ")
                    append(lastDistrict!!.districtName)
                    append(", ")
                    append(lastProvince!!.provinceName)
                }
                val action =
                    RegisterSellerFragmentDirections.actionRegisterSellerFragmentToSendOtpFragment(
                        uriAnh,
                        shopName,
                        shopAddress,
                        lastProvince!!.provinceID,
                        lastDistrict!!.districtID,
                        lastWard!!.wardCode
                    )
                controller.navigate(action)
            }

//            imgMap.setOnClickListener {
//                val bundle = bundleOf(
//                    StringConstants.ADDRESS_DETAIL to edtAddressShop.text.toString()
//                )
//                controller.navigate(R.id.action_registerSellerFragment_to_mapFragment2, bundle)
//            }

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

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}