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
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.utils.CalculateUtils

class OrderProductAdapter(
    private val productOrders: List<OrderFromCart>,
    private val orderFromBuyNow: OrderFromBuyNow?,
) :
    RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>() {
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
        position: Int
    ) {
        holder.binding.apply {
            if (productOrders.isNotEmpty()) {
                layoutItem.root.isVisible = false
                recyclerViewOrderItem.isVisible = true
                val itemOrder = productOrders[position]
                val orderProductSameShopAdapter = OrderProductSameShopAdapter(itemOrder.cartItems)
                tvShopName.text = itemOrder.cart.tenShop
                recyclerViewOrderItem.apply {
                    adapter = orderProductSameShopAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(MarginItemDecoration(20))
                }
                tvTotalPrice.text = buildString {
                    append(CalculateUtils.calcTotalPrice(itemOrder.cartItems))
                    append("$")
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
                        tvTotalPrice.text = buildString {
                            append(CalculateUtils.calcTotalPrice(order))
                            append("$")
                        }
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int = if (productOrders.isNotEmpty()) productOrders.size else 1

    class OrderProductViewHolder(val binding: LayoutProductOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}