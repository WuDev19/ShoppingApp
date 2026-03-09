package com.example.shoppingapp.ui.view.adapter

import android.text.TextPaint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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

class OrderProductAdapter(
    private val productOrders: List<OrderFromCart>,
    private val orderFromBuyNow: OrderFromBuyNow?,
    private val onClickVoucher: (Int) -> Unit
) : RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>() {

    private var isClickToShow = false
    fun setServices(listService: MutableList<ShippingMethod>) {
        if (orderFromBuyNow != null) {
            notifyItemChanged(0, listService)
        } else {
            val position = productOrders.indexOfFirst {
                it.cart.shopGhnId == listService.first().shopId
            }
            notifyItemChanged(position, listService)
        }
    }

    fun setVoucher(showVoucherModel: ShowVoucherModel) {
        if (orderFromBuyNow != null) {
            notifyItemChanged(0, showVoucherModel)
        } else {
            val position = productOrders.indexOfFirst {
                it.cart.maNB == showVoucherModel.sellerId
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
            var totalPriceWithoutShipFee = if (productOrders.isNotEmpty()) {
                CalculateUtils.calcTotalPrice(productOrders[position].cartItems)
            } else {
                CalculateUtils.calcTotalPrice(
                    orderFromBuyNow!!
                )
            }
            payloads.forEach { payload ->
                if (payload is MutableList<*>) {
                    val services = payload.filterIsInstance<ShippingMethod>()
                    totalPriceWithoutShipFee += services[0].feeServiceGhn.totalFee
                    holder.binding.apply {
                        if (orderFromBuyNow == null) {
                            tvTotalPrice.text = buildString {
                                append(totalPriceWithoutShipFee)
                                append("$")
                            }
                        } else {
                            layoutItem.apply {
                                tvTotalPrice.text = buildString {
                                    append(totalPriceWithoutShipFee)
                                    append("$")
                                }
                            }
                        }
                    }
                    holder.submitShippingMethod(services)
                } else if (payload is ShowVoucherModel) {
                    holder.binding.apply {
                        if (orderFromBuyNow == null) {
                            tvVoucherShop.text = payload.voucherValue
                        } else {
                            layoutItem.apply {
                                tvVoucherShop.text = payload.voucherValue
                            }
                        }
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
                }
            }
            showShippingMethod(this)
        }
    }

    override fun getItemCount(): Int = if (productOrders.isNotEmpty()) productOrders.size else 1

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