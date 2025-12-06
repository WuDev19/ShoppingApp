package com.example.shoppingapp.ui.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.CartItemLayoutBinding
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.ui.view.decoration.MarginItemDecoration
import com.example.shoppingapp.ui.view.decoration.SeparationItemDecoration
import com.example.shoppingapp.ui.viewmodel.product.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartAdapter(
    private val viewModel: CartViewModel,
    private val lifecycleScope: LifecycleCoroutineScope
) : PagingDataAdapter<Cart, CartAdapter.CartViewHolder>(diffCallBack) {

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

    class CartViewHolder(val binding: CartItemLayoutBinding) : ViewHolder(binding.root) {
        private val cartItemAdapter by lazy { CartItemAdapter() }
        init {
            binding.recyclerViewCartItemInCart.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = cartItemAdapter
                addItemDecoration(SeparationItemDecoration(1, R.color.grey))
            }
        }

        fun bind(lifecycleScope: LifecycleCoroutineScope, viewModel: CartViewModel, cart: Cart){
            binding.tvNameShop.text = cart.tenShop
            lifecycleScope.launch {
                cart.cartId?.let {
                    viewModel.getCartItemInCart(it).collectLatest { pagingData ->
                        cartItemAdapter.submitData(pagingData)
                    }
                }
            }
        }

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { cart ->
            holder.binding.apply {
                holder.bind(lifecycleScope, viewModel, cart)
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