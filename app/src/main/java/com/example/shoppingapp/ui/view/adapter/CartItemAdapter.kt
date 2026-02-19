package com.example.shoppingapp.ui.view.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.databinding.ProductInCartItemBinding
import com.example.shoppingapp.domain.model.CartItem

class CartItemAdapter :
    PagingDataAdapter<CartItem, CartItemAdapter.CartItemViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem.cartItemId == newItem.cartItemId
            }

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    class CartItemViewHolder(val binding: ProductInCartItemBinding) : ViewHolder(binding.root)

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.binding.apply {
                Glide.with(holder.binding.root)
                    .load(it.detailProduct?.anhDetail)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder)
                    .into(imgProduct)
                tvMota.text = it.detailProduct?.tenDetail
                tvColorSize.text = buildString {
                    append(it.detailProduct?.color)
                    append(", ")
                    append(it.detailProduct?.kichco)
                }
                if (it.giamGia == 0) {
                    tvCostDiscount.text = buildString {
                        append((it.detailProduct?.unitPrice?.times(it.soLuong!!)).toString())
                        append(StringConstants.PRICE)
                    }
                    tvCost.visibility = View.GONE
                } else {
                    tvCost.visibility = View.VISIBLE
                    val discount = (it.detailProduct?.unitPrice)?.minus(
                        ((it.giamGia?.times(it.detailProduct.unitPrice!!))?.div(
                            100
                        )!!)
                    )
                    tvCostDiscount.text = buildString {
                        append(discount.toString())
                        append(StringConstants.PRICE)
                    }
                    tvCost.text = buildString {
                        append(it.detailProduct?.unitPrice.toString())
                        append(StringConstants.PRICE)
                    }
                    tvCost.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                edtNumber.setText(it.soLuong.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ProductInCartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartItemViewHolder(binding)
    }

}