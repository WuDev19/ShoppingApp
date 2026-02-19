package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutPurchaseProductBinding

class MainPurchaseAdapter(private val orderProductAdapter: OrderProductAdapter) :
    RecyclerView.Adapter<MainPurchaseAdapter.MainPurchaseViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainPurchaseViewHolder {
        val binding = LayoutPurchaseProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainPurchaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainPurchaseViewHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int = 1

    class MainPurchaseViewHolder(val binding: LayoutPurchaseProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}