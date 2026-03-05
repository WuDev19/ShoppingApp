package com.example.shoppingapp.ui.view.adapter

import android.text.TextPaint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutProductOrderSameShopBinding
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.utils.CalculateUtils

class OrderProductSameShopAdapter :
    ListAdapter<CartItem, OrderProductSameShopAdapter.OrderProductSameShopViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(
                oldItem: CartItem,
                newItem: CartItem
            ): Boolean = oldItem.cartItemId == newItem.cartItemId

            override fun areContentsTheSame(
                oldItem: CartItem,
                newItem: CartItem
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderProductSameShopViewHolder {
        val binding = LayoutProductOrderSameShopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductSameShopViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderProductSameShopViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.apply {
            tvProductName.text = item.detailProduct.tenDetail ?: ""
            tvColorSize.text = buildString {
                append(item.detailProduct.color)
                append(", ")
                append(item.detailProduct.kichco)
            }
            tvPriceDCount.text = buildString {
                append(
                    CalculateUtils.calcDiscount(
                        item.giamGia,
                        item.detailProduct.unitPrice!!
                    )
                )
                append("$")
            }
            tvPrice.text = buildString {
                append(item.detailProduct.unitPrice)
                append("$")
            }
            tvPrice.paintFlags = TextPaint.STRIKE_THRU_TEXT_FLAG
            tvNumber.text = buildString {
                append("x")
                append(item.soLuong)
            }
            Glide.with(root.context)
                .load(item.detailProduct.anhDetail ?: "")
                .error(R.drawable.icon_not_found)
                .into(imgAvatar)
        }
    }

    class OrderProductSameShopViewHolder(val binding: LayoutProductOrderSameShopBinding) :
        RecyclerView.ViewHolder(binding.root)

}