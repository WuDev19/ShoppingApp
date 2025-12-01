package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.CategoryOfShopItemBinding
import com.example.shoppingapp.domain.model.CategoryAndQuantity

class CategoryQuantityAdapter (private val onItemClick: (String) -> Unit) :
    ListAdapter<CategoryAndQuantity, CategoryQuantityAdapter.CategoryQuantityViewHolder>(
        diffCallBack
    ) {

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<CategoryAndQuantity>() {
            override fun areItemsTheSame(
                oldItem: CategoryAndQuantity,
                newItem: CategoryAndQuantity
            ): Boolean = oldItem.tenDanhMuc == newItem.tenDanhMuc

            override fun areContentsTheSame(
                oldItem: CategoryAndQuantity,
                newItem: CategoryAndQuantity
            ): Boolean = oldItem == newItem
        }
    }

    class CategoryQuantityViewHolder(val binding: CategoryOfShopItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryQuantityViewHolder {
        val binding = CategoryOfShopItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryQuantityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryQuantityViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {data ->
            holder.binding.tvCategory.text = data.tenDanhMuc
            holder.binding.tvNumber.text = buildString {
                append("(")
                append(data.sl)
                append(")")
            }
            holder.binding.linearCategory.setOnClickListener {
                data.tenDanhMuc?.let { it1 -> onItemClick(it1) }
            }
        }
    }

}