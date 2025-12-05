package com.example.shoppingapp.ui.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.CartItemLayoutBinding
import com.example.shoppingapp.domain.model.Cart

class CartAdapter : PagingDataAdapter<Cart, CartAdapter.CartViewHolder>(diffCallBack) {

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<Cart>() {
            override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem.cartId == newItem.cartId
            }

            override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                return oldItem == newItem
            }

        }
    }

    class CartViewHolder(val binding: CartItemLayoutBinding) : ViewHolder(binding.root)

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {cart ->
            holder.binding.apply {
                tvNameShop.text = cart.tenShop
                //bind adapter cart item o day
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

}