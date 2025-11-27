package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.GridAndHorizontalProductItemBinding

class HorizontalAdapterInsideConcatAdapter(private val otherProductOfShopAdapter: OtherProductOfShopAdapter) :
    Adapter<HorizontalAdapterInsideConcatAdapter.HorizontalAdapterInsideConcatAdapterViewHolder>() {

    class HorizontalAdapterInsideConcatAdapterViewHolder(val binding: GridAndHorizontalProductItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalAdapterInsideConcatAdapterViewHolder {
        val binding = GridAndHorizontalProductItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HorizontalAdapterInsideConcatAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(
        holder: HorizontalAdapterInsideConcatAdapterViewHolder,
        position: Int
    ) {
        holder.binding.recyclerViewRelatedProductInside.apply {
            layoutManager = LinearLayoutManager(
                holder.binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = otherProductOfShopAdapter
        }
    }

}