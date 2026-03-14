package com.example.shoppingapp.ui.view.adapter

import android.text.TextPaint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutProductOrderItemBinding
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.domain.model.ShowVoucherModel
import com.example.shoppingapp.domain.model.ghn.ShippingMethod
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.utils.CalculateUtils
import kotlin.apply
import kotlin.math.min

class OrderProductAdapter(
    private val productOrders: List<OrderFromCart>,
    private val orderFromBuyNow: OrderFromBuyNow?,
    private val onClickVoucher: (Int) -> Unit,
    private val finalPrice: (Long, Int, Long, Int, Long) -> Unit,
    private val onWriteNote: (String, Int) -> Unit
) : RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>() {

    private var isClickToShow = false
    private val feeServices by lazy { mutableListOf<MutableList<ShippingMethod>>() }
    fun setServices(listService: MutableList<ShippingMethod>) {
        if (orderFromBuyNow != null) {
            notifyItemChanged(0, listService)
        } else {
            val position = productOrders.indexOfFirst {
                it.cart.shopGhnId == listService.first().shopId
            }
            notifyItemChanged(position, listService)
        }
        feeServices.add(listService)
    }

    fun setVoucher(showVoucherModel: ShowVoucherModel) {
        if (orderFromBuyNow != null) {
            notifyItemChanged(0, showVoucherModel)
        } else {
            val position = productOrders.indexOfFirst {
                it.cart.shopGhnId == showVoucherModel.shopGhnId
            }
            notifyItemChanged(position, showVoucherModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderProductViewHolder {
        val binding = LayoutProductOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderProductViewHolder,
        position: Int,
        payloads: List<Any?>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val totalPrice: Long = if (productOrders.isNotEmpty()) { //tổng tiền ban đầu
                CalculateUtils.calcTotalPrice(productOrders[position].cartItems)
            } else {
                CalculateUtils.calcTotalPrice(
                    orderFromBuyNow!!
                )
            }
            payloads.forEach { payload ->
                if (payload is MutableList<*>) {
                    val services = payload.filterIsInstance<ShippingMethod>()
                    holder.submitShippingMethod(services)
                } else if (payload is ShowVoucherModel) {
                    holder.binding.apply {
                        var finalTotalPrice: Long = 0
                        var discountValue: Int
                        var priceWithoutShip: Long = 0
                        if (payload.voucherType == "Percent") {
                            discountValue = (totalPrice * payload.voucherOriginValue / 100).toInt()
                            finalTotalPrice += totalPrice - min(
                                payload.maxDiscount,
                                discountValue
                            ).toLong()
                            priceWithoutShip = finalTotalPrice
                        } else {
                            discountValue = payload.voucherOriginValue
                            finalTotalPrice += totalPrice - discountValue
                            priceWithoutShip = finalTotalPrice
                        }
                        if (orderFromBuyNow == null) {
                            val service = feeServices
                                .firstOrNull { shippingMethods ->
                                    shippingMethods[0].shopId == productOrders[position].cart.shopGhnId
                                }
                                ?.firstOrNull { shippingMethod -> shippingMethod.isSelected }
                            finalTotalPrice += service?.feeServiceGhn?.totalFee ?: 0
                            tvVoucherShop.text = payload.voucherValue
                            tvTotalPrice.text = finalTotalPrice.toString()
                        } else {
                            finalTotalPrice += feeServices[0].firstOrNull { shippingMethod ->
                                shippingMethod.isSelected
                            }?.feeServiceGhn?.totalFee ?: 0
                            layoutItem.apply {
                                tvVoucherShop.text = payload.voucherValue
                                tvTotalPrice.text = finalTotalPrice.toString()
                            }
                        }
                        finalPrice(
                            finalTotalPrice,
                            payload.shopGhnId,
                            totalPrice,
                            discountValue,
                            priceWithoutShip
                        )
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: OrderProductViewHolder,
        position: Int,
    ) {
        holder.binding.apply {
            if (productOrders.isNotEmpty()) {
                layoutItem.root.isVisible = false
                recyclerViewOrderItem.isVisible = true
                val itemOrder = productOrders[position]
                holder.submitOrderSameShop(itemOrder.cartItems)
                tvShopName.text = itemOrder.cart.tenShop
                linearVoucher.setOnClickListener {
                    onClickVoucher(itemOrder.cart.maNB)
                }
                edtNote.addTextChangedListener { text ->
                    onWriteNote(text.toString(), itemOrder.cart.shopGhnId)
                }
            } else {
                orderFromBuyNow?.let { order ->
                    layoutItem.root.isVisible = true
                    recyclerViewOrderItem.isVisible = false
                    layoutItem.apply {
                        tvShopName.text = order.shopName
                        tvProductName.text = order.productChiTiet.tenDetail
                        tvColorSize.text = buildString {
                            append(order.productChiTiet.color)
                            append(", ")
                            append(order.productChiTiet.kichco)
                        }
                        tvPriceDCount.text = buildString {
                            append(
                                CalculateUtils.calcDiscount(
                                    order.discount,
                                    order.productChiTiet.unitPrice!!
                                )
                            )
                            append("$")
                        }
                        tvPrice.text = buildString {
                            append(order.productChiTiet.unitPrice)
                            append("$")
                        }
                        tvPrice.paintFlags = TextPaint.STRIKE_THRU_TEXT_FLAG
                        tvNumber.text = buildString {
                            append("x")
                            append(order.number)
                        }
                    }
                    linearVoucher.setOnClickListener {
                        onClickVoucher(order.sellerId)
                    }
                    edtNote.addTextChangedListener { text ->
                        onWriteNote(text.toString(), order.shopGhnId)
                    }
                }
            }
            showShippingMethod(this)
        }
    }

    override fun getItemCount(): Int = if (productOrders.isNotEmpty()) productOrders.size else 1

    // TODO: fix cai click sau
    private fun showShippingMethod(binding: LayoutProductOrderItemBinding) {
        binding.apply {
            linearShipping.setOnClickListener {
                if (!isClickToShow) {
                    isClickToShow = true
                    imgRotateShip.animate()
                        .rotation(90f)
                        .setDuration(500)
                        .withEndAction {
                            recyclerViewShippingMethod.isVisible = true
                        }
                } else {
                    isClickToShow = false
                    imgRotateShip.animate()
                        .rotation(0f)
                        .setDuration(500)
                        .withEndAction {
                            recyclerViewShippingMethod.isVisible = false
                        }
                }
            }
        }
    }

    class OrderProductViewHolder(val binding: LayoutProductOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val orderProductSameShopAdapter = OrderProductSameShopAdapter()
        private val shippingMethodAdapter = ShippingMethodAdapter()

        init {
            binding.apply {
                recyclerViewOrderItem.apply {
                    adapter = orderProductSameShopAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(MarginItemDecoration(20))
                }
                recyclerViewShippingMethod.apply {
                    adapter = shippingMethodAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }

        fun submitOrderSameShop(list: List<CartItem>) {
            orderProductSameShopAdapter.submitList(list)
        }

        fun submitShippingMethod(list: List<ShippingMethod>) {
            shippingMethodAdapter.submitList(list)
        }
    }

}