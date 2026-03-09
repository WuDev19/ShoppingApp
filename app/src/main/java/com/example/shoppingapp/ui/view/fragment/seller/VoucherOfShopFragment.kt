package com.example.shoppingapp.ui.view.fragment.seller

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.databinding.FragmentVoucherOfShopBinding
import com.example.shoppingapp.domain.model.ShopVouchers
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.ShopVoucherAdapter
import com.example.shoppingapp.ui.viewmodel.seller.VoucherOfShopViewModel
import com.example.shoppingapp.utils.StringConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoucherOfShopFragment(private val onClickVoucher: (Int, String) -> Unit) :
    BottomSheetDialogFragment() {
    private val TAG = VoucherOfShopFragment::class.simpleName
    private val viewModel: VoucherOfShopViewModel by viewModels()
    private var _binding: FragmentVoucherOfShopBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var shopVoucherAdapter: ShopVoucherAdapter
    private var voucherId = -1
    private var voucherValue = ""
    private var listVoucher: List<ShopVouchers> = mutableListOf()

    private val sellerId: Int? by lazy { arguments?.getInt(StringConstants.ID_SELLER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoucherOfShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sellerId?.let {
            viewModel.getShopVouchers(it)
        }
        shopVoucherAdapter = ShopVoucherAdapter(emptyList()) { voucherId, voucherValue ->
            Log.e(TAG, "onViewCreated: $voucherId")
            if (voucherId != -1) {
                this.voucherId = voucherId
                this.voucherValue = voucherValue
            }
        }

        binding.apply {
            recyclerViewShopVoucher.apply {
                adapter = shopVoucherAdapter
                layoutManager = LinearLayoutManager(requireContext())
                isNestedScrollingEnabled = true
            }
            btnApplyVoucher.setOnClickListener {
                onClickVoucher(voucherId, voucherValue)
                dismiss()
            }
            btnFind.setOnClickListener {
                val voucherCode = edtVoucherCode.text.toString().trim()
                if (voucherCode.isEmpty()) {
                    return@setOnClickListener
                }
                val searchListVoucher = listVoucher.filter {
                    it.voucherCode == voucherCode
                }
                shopVoucherAdapter.setVouchers(searchListVoucher)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shopVoucherState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> binding.progressBar.isVisible = true
                        is StateListener.Error -> {
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                            binding.progressBar.isVisible = false
                        }

                        is StateListener.Success -> {
                            val result = it.result
                            binding.progressBar.isVisible = false
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.result}")
                            listVoucher = result
                            shopVoucherAdapter.setVouchers(result)
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