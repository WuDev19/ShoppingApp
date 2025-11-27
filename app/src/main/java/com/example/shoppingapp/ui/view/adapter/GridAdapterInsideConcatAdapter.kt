package com.example.shoppingapp.ui.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.GridAndHorizontalProductItemBinding

class GridAdapterInsideConcatAdapter(private val productAdapter: ProductAdapter) :
    Adapter<GridAdapterInsideConcatAdapter.GridAdapterInsideConcatAdapterViewHolder>() {

    class GridAdapterInsideConcatAdapterViewHolder(val binding: GridAndHorizontalProductItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridAdapterInsideConcatAdapterViewHolder {
        val binding = GridAndHorizontalProductItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GridAdapterInsideConcatAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: GridAdapterInsideConcatAdapterViewHolder, position: Int) {
        holder.binding.recyclerViewRelatedProductInside.apply {
            layoutManager = GridLayoutManager(holder.binding.root.context, 2)
            adapter = productAdapter
        }
    }

}