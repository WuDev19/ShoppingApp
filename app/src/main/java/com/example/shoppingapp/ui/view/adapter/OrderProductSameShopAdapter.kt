package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutProductOrderSameShopBinding

class OrderProductSameShopAdapter(private val productOrderSameShop: List<Any>) :
    RecyclerView.Adapter<OrderProductSameShopAdapter.OrderProductSameShopViewHolder>() {
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
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = productOrderSameShop.size

    class OrderProductSameShopViewHolder(val binding: LayoutProductOrderSameShopBinding) :
        RecyclerView.ViewHolder(binding.root)

}