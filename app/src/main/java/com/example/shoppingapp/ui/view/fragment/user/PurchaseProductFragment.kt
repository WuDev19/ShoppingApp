package com.example.shoppingapp.ui.view.fragment.user

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.databinding.FragmentPurchaseProductBinding
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.model.FinalPrice
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.domain.model.ShowVoucherModel
import com.example.shoppingapp.domain.model.VoucherOrder
import com.example.shoppingapp.domain.model.ghn.ShippingMethod
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.view.adapter.OrderProductAdapter
import com.example.shoppingapp.ui.view.fragment.seller.VoucherOfShopFragment
import com.example.shoppingapp.ui.viewmodel.user.PurchaseProductViewModel
import com.example.shoppingapp.utils.CalculateUtils
import com.example.shoppingapp.utils.CheckNavigation
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseProductFragment : Fragment() {

    private val TAG = PurchaseProductFragment::class.simpleName
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
    private lateinit var orderProductAdapter: OrderProductAdapter
    private var totalPrice: Long = 0
    private var address: AddressItem? = null
    private val voucherOrders by lazy { mutableMapOf<Int, VoucherOrder>() }
    private val shippingMethods by lazy { mutableMapOf<Int, ShippingMethod>() }
    private val finalPrices by lazy { mutableMapOf<Int, FinalPrice>() }
    private val userNotes by lazy { mutableMapOf<Int, String>() }

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
            OrderProductAdapter(
                listOrder ?: emptyList(),
                orderFromBuyNow,
                onClickVoucher = { sellerId ->
                    val voucherOfShopFragment =
                        VoucherOfShopFragment { voucherId, voucherValue, shopGhnId, voucherOriginValue, maxDiscount, voucherType ->
                            if (voucherId != -1) {
                                voucherOrders[shopGhnId] =
                                    VoucherOrder(voucherId, voucherValue, shopGhnId)
                                val showVoucherModel =
                                    ShowVoucherModel(
                                        voucherId,
                                        voucherValue,
                                        shopGhnId,
                                        voucherOriginValue,
                                        maxDiscount,
                                        voucherType
                                    )
                                orderProductAdapter.setVoucher(showVoucherModel)
                            }
                        }
                    val bundle = bundleOf(
                        StringConstants.ID_SELLER to sellerId
                    )
                    voucherOfShopFragment.arguments = bundle
                    voucherOfShopFragment.show(childFragmentManager, voucherOfShopFragment.tag)
                },
                finalPrice = { finalTotalPrice, shopGhnId, totalPriceOrigin, discountValue, priceWithoutShip ->
                    finalPrices[shopGhnId] =
                        FinalPrice(
                            finalTotalPrice,
                            shopGhnId,
                            totalPriceOrigin,
                            discountValue,
                            priceWithoutShip
                        )
                },
                onWriteNote = {
                    note, shopGhnId -> userNotes[shopGhnId] = note
                })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getAddress.collectLatest { addresses ->
                        if (addresses.isNotEmpty()) {
                            address = addresses
                                .filter { addressItem -> addressItem.defaultAddress }
                                .ifEmpty {
                                    listOf(addresses.first())
                                }.firstOrNull()
                            displayAddress(address)
                            handleServiceIfOrderFromBuyNowNotNull()
                            handleServiceIfListOrderNotEmpty()
                        }
                    }
                }
                launch {
                    viewModel.serviceState.collect {
                        when (it) {
                            is StateListener.Idle -> Unit // TODO:
                            is StateListener.Loading -> Unit //đoạn này cho một cái shimmer block cả màn hình nào xong mới cho bấm vào shopvoucher
                            is StateListener.Error -> {
                                Log.e(
                                    StringConstants.DEBUG,
                                    "onViewCreated: ${it.e?.message}"
                                )
                            }

                            is StateListener.Success<ShippingMethod> -> {
                                val shippingMethod = it.result
                                shippingMethods[shippingMethod.shopId] = shippingMethod
                                orderProductAdapter.setServices(mutableListOf(shippingMethod))
                            }
                        }
                    }
                }
                launch {
                    controller.currentBackStackEntry?.savedStateHandle?.getStateFlow<AddressItem?>(
                        StringConstants.PURCHASE_TO_ADDRESS,
                        null
                    )?.collectLatest { addressItem ->
                        addressItem?.let {
                            address = it
                            displayAddress(it)
                        }
                    }
                }
                launch {
                    viewModel.createOrderState.collectLatest {
                        when (it) {
                            is StateListener.Idle -> Unit
                            is StateListener.Loading -> Unit
                            is StateListener.Error -> Log.e(TAG, "onViewCreated: ${it.e?.message}")
                            is StateListener.Success<Order> -> {
                                Log.e(TAG, "onViewCreated: ${it.result}")
                            }
                        }
                    }
                }
                totalPrice = async(Dispatchers.Default) {
                    orderFromBuyNow?.let {
                        CalculateUtils.calcTotalPrice(it)
                    } ?: CalculateUtils.calcTotalPriceFromCart(listOrder ?: emptyList())
                }.await()
                binding.apply {
                    tvTotalPrice.text = buildString {
                        append(totalPrice)
                        append("$")
                    }
                    tvTotalBill.text = buildString {
                        append(totalPrice)
                        append("$")
                    }
                }
            }
        }

        binding.apply {
            linearAddress.setOnClickListener {
                val bundle = bundleOf(
                    StringConstants.PURCHASE_TO_ADDRESS to true
                )
                CheckNavigation.navigateTo(
                    controller,
                    R.id.action_purchaseProductFragment_to_addressFragment2,
                    R.id.action_purchaseProductFragment2_to_addressFragment3,
                    bundle
                )
            }

            recyclerViewOrder.apply {
                adapter = orderProductAdapter
                layoutManager = LinearLayoutManager(context)
            }

            radioCod.setOnClickListener {
                radioCod.isChecked = true
                radioVnPay.isChecked = false
            }

            radioVnPay.setOnClickListener {
                radioVnPay.isChecked = true
                radioCod.isChecked = false
            }

            imgBack.setOnClickListener {
                controller.popBackStack()
            }

            btnBuy.setOnClickListener {
                if (radioVnPay.isChecked) {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setIcon(R.drawable.icon_avatar)
                        .setTitle("Shopping App")
                        .setMessage("If you choose online payment, you must pay first")
                        .setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
                        .setPositiveButton("Ok") { dialogInterface, _ ->

                        }
                        .create()
                    dialog.show()
                    dialog.window?.apply {
                        setBackgroundDrawableResource(R.drawable.custom_dialog)
                    }
                } else {
                    if (address == null || shippingMethods.isEmpty()) {
                        ToastUtils.showToast(requireContext(), "Not ready, wait a minute")
                        return@setOnClickListener
                    }
                    orderFromBuyNow?.let {
                        viewModel.createOrderWithCod(
                            orderFromBuyNow = it,
                            addressItem = address!!,
                            shippingMethods = shippingMethods,
                            voucherOrders = voucherOrders,
                            finalPrices = finalPrices,
                            note = userNotes,
                            paymentMethod = "COD",
                            isCod = true
                        )
                    } ?: viewModel.createOrderWithCod(
                        listOrder = listOrder,
                        addressItem = address!!,
                        shippingMethods = shippingMethods,
                        voucherOrders = voucherOrders,
                        finalPrices = finalPrices,
                        note = userNotes,
                        paymentMethod = "COD",
                        isCod = true
                    )

                }
            }
        }

    }

    private fun handleServiceIfListOrderNotEmpty() {
        listOrder?.let {
            it.forEach { orderFromCart ->
                var totalWeight = 0
                var cost = 0
                orderFromCart.cartItems.forEach { cartItem ->
                    totalWeight += cartItem.detailProduct.weight!!
                    val sum =
                        (cartItem.soLuong * cartItem.detailProduct.unitPrice!!) -
                                ((cartItem.soLuong * cartItem.detailProduct.unitPrice!!) * cartItem.giamGia / 100)
                    cost += sum
                }
                val shopGhnId = orderFromCart.cart.shopGhnId
                val fromDistrict = orderFromCart.cart.districtIdShop
                val toDistrict = address?.districtId
                val params = mapOf(
                    "shopId" to shopGhnId.toString(),
                    "fromDistrictId" to fromDistrict.toString(),
                    "toDistrictId" to toDistrict.toString()
                )
                val calculateFeeGhnRequest = CalculateFeeGhnRequest(
                    to_ward_code = address!!.wardCode,
                    to_district_id = address!!.districtId,
                    weight = totalWeight,
                    insurance_value = if (cost >= 1000000) cost else 0,
                    cod_value = if (binding.radioCod.isChecked) cost else 0
                )
                viewModel.getService(params, calculateFeeGhnRequest, shopGhnId)
            }
        }
    }

    private fun handleServiceIfOrderFromBuyNowNotNull() {
        orderFromBuyNow?.let {
            val params = mapOf(
                "shopId" to it.shopGhnId.toString(),
                "fromDistrictId" to it.districtId.toString(),
                "toDistrictId" to address!!.districtId.toString()
            )
            val costDiscount =
                (it.number * it.productChiTiet.unitPrice!!) -
                        (it.number * it.productChiTiet.unitPrice!!) * it.discount / 100
            val calculateFeeGhnRequest = CalculateFeeGhnRequest(
                to_ward_code = address!!.wardCode,
                to_district_id = address!!.districtId,
                weight = it.productChiTiet.weight!!,
                insurance_value = if (costDiscount >= 1000000) costDiscount else 0,
                cod_value = if (binding.radioCod.isChecked) costDiscount else 0
            )
            viewModel.getService(params, calculateFeeGhnRequest, it.shopGhnId)
        }
    }

    private fun displayAddress(address: AddressItem?) {
        binding.apply {
            tvAddress.text = address?.addressName ?: ""
            tvUsername.text = address?.name ?: ""
            tvPhone.text = address?.sdt ?: ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}