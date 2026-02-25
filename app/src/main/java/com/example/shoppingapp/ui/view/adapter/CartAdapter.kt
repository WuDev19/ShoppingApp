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
import com.example.shoppingapp.databinding.CartItemLayoutBinding
import com.example.shoppingapp.databinding.ProductInCartItemBinding
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.CartUiModel
import com.example.shoppingapp.utils.StringConstants

class CartAdapter(
    private val onCheckBoxItem: (CartUiModel, Boolean) -> Unit
) :
    PagingDataAdapter<CartUiModel, ViewHolder>(diffCallBack) {
    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<CartUiModel>() {
            override fun areItemsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return (oldItem is Cart && newItem is Cart && oldItem.cartId == newItem.cartId) ||
                        (oldItem is CartItem && newItem is CartItem && oldItem.cartItemId == newItem.cartItemId)
            }

            override fun areContentsTheSame(oldItem: CartUiModel, newItem: CartUiModel): Boolean {
                return oldItem == newItem
            }

        }
        private const val TYPE_CART = 0
        private const val TYPE_CART_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Cart -> TYPE_CART
            is CartItem -> TYPE_CART_ITEM
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    class CartViewHolder(val binding: CartItemLayoutBinding) : ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.apply {
                tvNameShop.text = cart.tenShop
                Glide.with(root.context)
                    .load(cart.avtShop)
                    .error(R.drawable.icon_avatar)
                    .into(imgAvatar)
            }
        }
    }

    class CartItemViewHolder(val binding: ProductInCartItemBinding) :
        ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            cartItem.let {
                binding.apply {
                    Glide.with(root)
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
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item is Cart) {
            (holder as CartViewHolder).bind(item)
        } else if (item is CartItem) {
            (holder as CartItemViewHolder).bind(item)
            holder.binding.apply {
                checkBoxProduct.setOnClickListener {
                    val isChecked = checkBoxProduct.isChecked
                    if (isChecked) {
                        onCheckBoxItem(item, true)
                    } else {
                        onCheckBoxItem(item, false)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_CART) {
            val binding = CartItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CartViewHolder(binding)
        } else {
            val binding = ProductInCartItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            CartItemViewHolder(binding)
        }
    }

}