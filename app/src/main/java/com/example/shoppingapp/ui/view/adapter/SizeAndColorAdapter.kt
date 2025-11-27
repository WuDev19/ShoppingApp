package com.example.shoppingapp.ui.view.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.SizeAndColorItemBinding
import com.example.shoppingapp.domain.model.SizeColorModel

class SizeAndColorAdapter(
    private val listData: List<SizeColorModel>,
    private val onItemClick: (String, Boolean) -> Unit
) : Adapter<SizeAndColorAdapter.SizeAndColorViewHolder>() {

    private val positionTrace: MutableSet<Int> = mutableSetOf()

    class SizeAndColorViewHolder(val binding: SizeAndColorItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAndColorViewHolder {
        val binding =
            SizeAndColorItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SizeAndColorViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: SizeAndColorViewHolder, position: Int) {
        val data = listData[position]
        holder.binding.apply {
            //notify lai position cu
            if (data.selected) {
                cardSizeColor.strokeWidth = 0
                data.selected = false
            }
            tvData.text = data.data
            tvData.setOnClickListener {
                if (!data.selected) {
                    data.selected = true
                    cardSizeColor.setStrokeColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                holder.binding.root.context,
                                R.color.button_color
                            )
                        )
                    )
                    cardSizeColor.strokeWidth = 2
                    if (positionTrace.isNotEmpty()) {
                        notifyItemChanged(positionTrace.elementAt(0))
                    }
                    positionTrace.clear()
                    positionTrace.add(position)
                } else {
                    data.selected = false
                    cardSizeColor.strokeWidth = 0
                    positionTrace.clear() //bam lai vi tri hien tai thi clear di de lam moi set, tranh duoc bug khi bam ca 2 cai cung bi len mau xanh
                }
                onItemClick(data.data!!, data.selected)
            }

        }
    }

}