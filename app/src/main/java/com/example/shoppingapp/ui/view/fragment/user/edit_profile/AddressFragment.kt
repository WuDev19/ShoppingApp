package com.example.shoppingapp.ui.view.fragment.user.edit_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.BottomNavigationUtils
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentAddressBinding
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.ui.view.adapter.UserAddressAdapter
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.viewmodel.user.edit_profile.AddressViewModel
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private val viewModel: AddressViewModel by viewModels()
    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private var addressItem: AddressItem? = null
    private val userAddressAdapter by lazy {
        UserAddressAdapter(
            isFromPurchase ?: false,
            onItemClick = { addressItem ->
                val bundle = bundleOf(
                    StringConstants.ADDRESS_ITEM to addressItem
                )
                CheckNavigation.navigateTo(
                    controller,
                    R.id.action_addressFragment2_to_editAddressFragment2,
                    R.id.action_addressFragment3_to_editAddressFragment3,
                    bundle,
                    R.id.action_addressFragment_to_editAddressFragment
                )
            },
            onSelectAddress = { addressItem ->
                if (isFromPurchase != null && isFromPurchase == true) {
                    Log.e("PurchaseProductFragment", "select $addressItem")
                    this.addressItem = addressItem
                }
            })
    }
    private val controller by lazy { findNavController() }
    private val viewPager2: ViewPager2 by lazy { requireActivity().findViewById(R.id.view_pager2) }
    private val bottomNavigationView: BottomNavigationView by lazy {
        requireActivity().findViewById(
            R.id.bottom_navigation_view
        )
    }
    private val isFromPurchase by lazy {
        arguments?.getBoolean(StringConstants.PURCHASE_TO_ADDRESS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAddressState.collectLatest {
                    userAddressAdapter.submitList(it)
                }
            }
        }

        binding.apply {
            if (isFromPurchase != null && isFromPurchase == true) {
                btnSelectAddress.isVisible = true
            }
            recyclerViewUserAddress.apply {
                adapter = userAddressAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(MarginItemDecoration(10))
            }
            imgBack.setOnClickListener {
                controller.popBackStack()
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
                ViewPagerUtils.enableViewPager(viewPager2, controller)
            }
            btnAddAddress.setOnClickListener {
                CheckNavigation.navigateTo(
                    controller,
                    R.id.action_addressFragment2_to_editAddressFragment2,
                    R.id.action_addressFragment3_to_editAddressFragment3,
                    R.id.action_addressFragment_to_editAddressFragment
                )
            }
            btnSelectAddress.setOnClickListener {
                Log.e("PurchaseProductFragment", "btn select $addressItem")
                addressItem?.let {
                    controller.previousBackStackEntry?.savedStateHandle?.set(
                        StringConstants.PURCHASE_TO_ADDRESS,
                        it
                    )
                }
                controller.popBackStack()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}