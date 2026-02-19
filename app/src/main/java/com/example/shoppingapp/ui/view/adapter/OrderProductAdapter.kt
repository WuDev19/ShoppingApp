package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutProductOrderItemBinding

class OrderProductAdapter(
    private val productOrders: List<Any>,
    private val orderProductSameShopAdapter: OrderProductSameShopAdapter
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

    }

    override fun getItemCount(): Int = productOrders.size

    class OrderProductViewHolder(val binding: LayoutProductOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}