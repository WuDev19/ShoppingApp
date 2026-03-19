package com.example.shoppingapp.ui.view.adapter

import android.text.TextPaint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutProductOrderSameShopBinding
import com.example.shoppingapp.domain.model.OrderItemDetail

class OrderItemAdapter(private var orderItems: List<OrderItemDetail>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    fun setOrderItems(orderItems: List<OrderItemDetail>){
        this.orderItems = orderItems;
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemViewHolder {
        val binding = LayoutProductOrderSameShopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderItemViewHolder,
        position: Int
    ) {
        val orderItem = orderItems[position]
        holder.binding.apply {
            Glide.with(root.context)
                .load(orderItem.detailProduct.anhDetail)
                .error(R.drawable.icon_not_found)
                .into(imgAvatar)
            tvProductName.text = orderItem.detailProduct.tenDetail
            tvColorSize.text = buildString {
                append(orderItem.detailProduct.color)
                append(", ")
                append(orderItem.detailProduct.kichco)
            }
            tvPrice.text = buildString {
                append(orderItem.detailProduct.unitPrice)
                append("$")
            }
            tvPrice.paintFlags = TextPaint.STRIKE_THRU_TEXT_FLAG
            tvPriceDCount.text = buildString {
                append(orderItem.price)
                append("$")
            }
            tvNumber.text = buildString {
                append("x")
                append(orderItem.number.toString())
            }
        }
    }

    override fun getItemCount(): Int = orderItems.size

    class OrderItemViewHolder(val binding: LayoutProductOrderSameShopBinding) :
        RecyclerView.ViewHolder(binding.root)
}