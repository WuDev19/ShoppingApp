package com.example.shoppingapp.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutSelectCategoryItemBinding
import com.example.shoppingapp.domain.model.Category

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val onChooseCategory: (Category) -> Unit
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private val density by lazy { context.resources.displayMetrics.density }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding = LayoutSelectCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        val item = categories[position]
        holder.binding.apply {
            tvCategory.text = item.category
            linearCategory.setOnClickListener {
                if (item.selected) {
                    cardCategory.apply {
                        setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_grey))
                    }
                    tvCategory.setTextColor(ContextCompat.getColor(context, R.color.black))
                    item.selected = false
                } else {
                    cardCategory.apply {
                        setCardBackgroundColor(ContextCompat.getColor(context, R.color.button_color))
                    }
                    tvCategory.setTextColor(ContextCompat.getColor(context, R.color.white))
                    item.selected = true
                }
                onChooseCategory(item)
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(val binding: LayoutSelectCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}