package com.example.shoppingapp.ui.view.adapter

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.CategoriesItemInStoreBinding

class CategoryStoreAdapter(
    private val listName: List<String>,
    private val onItemClick: (String) -> Unit
) : Adapter<CategoryStoreAdapter.CategoryStoreViewHolder>() {

    class CategoryStoreViewHolder(val binding: CategoriesItemInStoreBinding) :
        ViewHolder(binding.root)

    private val listSaveTrace: MutableSet<Int> = mutableSetOf()
    private var checkNavigate: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryStoreViewHolder {
        val binding =
            CategoriesItemInStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryStoreViewHolder(binding)
    }

    override fun getItemCount(): Int = listName.size

    override fun onBindViewHolder(holder: CategoryStoreViewHolder, position: Int) {

        val name = listName[position]

        holder.binding.apply {
            tvCategoryItem.text = name

            //case nay de notify khi chuyen doi cac category
            if (listSaveTrace.isNotEmpty()) {
                tvCategoryItem.setTypeface(null, Typeface.NORMAL)
                tvCategoryItem.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.black
                        )
                    )
                )
                viewCategoryItem.setBackgroundResource(R.color.grey)
                viewCategoryItem.scaleX = 1f
                viewCategoryItem.scaleY = 1f
            }

            //case nay khi moi khoi tao va mac dinh hien thi o vi tri 0
            if (position == 0 && listSaveTrace.isEmpty()) {
                tvCategoryItem.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.button_color
                        )
                    )
                )
                tvCategoryItem.setTypeface(null, Typeface.BOLD)
                viewCategoryItem.setBackgroundResource(R.color.button_color)
                viewCategoryItem.scaleX = 5f
                viewCategoryItem.scaleY = 5f
                listSaveTrace.add(position)
            }

            //case khi click vao cac nut de navigate den man hinh khac trong store fragment
            if (position == listSaveTrace.elementAt(0) && checkNavigate) {
                checkNavigate = false
                tvCategoryItem.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.button_color
                        )
                    )
                )
                tvCategoryItem.setTypeface(null, Typeface.BOLD)
                viewCategoryItem.setBackgroundResource(R.color.button_color)
                viewCategoryItem.scaleX = 5f
                viewCategoryItem.scaleY = 5f
                listSaveTrace.add(position)
            }

            tvCategoryItem.setOnClickListener {
                onItemClick(name)
                if (listSaveTrace.isNotEmpty() && position != listSaveTrace.elementAt(0)) {
                    notifyItemChanged(listSaveTrace.elementAt(0))
                    tvCategoryItem.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.button_color
                            )
                        )
                    )
                    tvCategoryItem.setTypeface(null, Typeface.BOLD)
                    viewCategoryItem.setBackgroundResource(R.color.button_color)
                    viewCategoryItem.scaleX = 5f
                    viewCategoryItem.scaleY = 5f
                    listSaveTrace.clear()
                    listSaveTrace.add(position)
                }
            }
        }
    }

    //khi click tu home category sang
    fun setChooseTab(position: Int) {
        if (listSaveTrace.isNotEmpty() && position != listSaveTrace.elementAt(0)) {
            notifyItemChanged(listSaveTrace.elementAt(0))
        }
        listSaveTrace.clear()
        listSaveTrace.add(position)
    }

    //khi click vao san pham -> chi tiet san pham -> clear listtrace de khi back lai no hien mac dinh la all mau xanh
    fun setListSaveTrace() {
        if (listSaveTrace.isNotEmpty()) {
            checkNavigate = true
        }
    }

}