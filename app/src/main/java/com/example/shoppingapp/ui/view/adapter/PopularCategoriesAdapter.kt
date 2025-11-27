package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.CustomPopularCategoriesItemBinding
import com.example.shoppingapp.domain.model.PopularCategories

class PopularCategoriesAdapter(private val listItem: List<PopularCategories>, private val onItemClick: (String) -> Unit) :
    Adapter<PopularCategoriesAdapter.PopularCategoriesViewHolder>() {

    class PopularCategoriesViewHolder(val binding: CustomPopularCategoriesItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularCategoriesViewHolder {
        val binding = CustomPopularCategoriesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PopularCategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: PopularCategoriesViewHolder, position: Int) {
        val product = listItem[position]
        holder.binding.apply {
            picture.setImageResource(product.intRes)
            name.text = product.name
            picture.setOnClickListener {
                onItemClick(product.name)
            }
        }
    }

}