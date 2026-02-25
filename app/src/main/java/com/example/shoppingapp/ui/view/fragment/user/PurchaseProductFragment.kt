package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentPurchaseProductBinding
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.ui.view.adapter.MainPurchaseAdapter
import com.example.shoppingapp.ui.view.adapter.OrderProductAdapter
import com.example.shoppingapp.ui.viewmodel.user.PurchaseProductViewModel
import com.example.shoppingapp.utils.CalculateUtils
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseProductFragment : Fragment() {

    private val viewModel: PurchaseProductViewModel by viewModels()
    private var _binding: FragmentPurchaseProductBinding? = null
    private val binding
        get() = _binding!!
    private val controller by lazy { findNavController() }
    private val listOrder by lazy {
        arguments?.let {
            BundleCompat.getParcelableArrayList(
                it,
                StringConstants.LIST_ORDER,
                OrderFromCart::class.java
            )
        }
    }
    private val orderFromBuyNow by lazy {
        arguments?.let {
            BundleCompat.getParcelable(
                it,
                StringConstants.ORDER_BUY_NOW,
                OrderFromBuyNow::class.java
            )
        }
    }
    private lateinit var mainPurchaseAdapter: MainPurchaseAdapter
    private lateinit var orderProductAdapter: OrderProductAdapter
    private var totalPrice: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderProductAdapter =
            OrderProductAdapter(listOrder ?: emptyList(), orderFromBuyNow)
        mainPurchaseAdapter = MainPurchaseAdapter(orderProductAdapter) {
            CheckNavigation.navigateTo(
                controller,
                R.id.action_purchaseProductFragment_to_addressFragment2,
                R.id.action_purchaseProductFragment2_to_addressFragment3
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getAddress.collectLatest { addresses ->
                        if (addresses.isNotEmpty()) {
                            val result =
                                addresses.filter { addressItem -> addressItem.defaultAddress }
                                    .ifEmpty {
                                        listOf(addresses.first())
                                    }
                            mainPurchaseAdapter.setAddress(result.first())
                        }
                    }
                }
                totalPrice = async(Dispatchers.Default) {
                    orderFromBuyNow?.let {
                        CalculateUtils.calcTotalPrice(it)
                    } ?: CalculateUtils.calcTotalPriceFromCart(listOrder ?: emptyList())
                }.await()
                mainPurchaseAdapter.setTotalPrice(totalPrice)
                binding.tvTotalPrice.text = buildString {
                    append(totalPrice)
                    append("$")
                }
            }
        }

        binding.apply {
            recyclerViewPurchase.apply {
                adapter = mainPurchaseAdapter
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

}