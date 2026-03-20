package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutStatusOrderItemBinding
import com.example.shoppingapp.domain.model.StatusOrder

class StatusOrderAdapter(
    private var listStatusOrder: List<StatusOrder>,
    private val onStatusClick: (String) -> Unit
) : RecyclerView.Adapter<StatusOrderAdapter.StatusOrderViewHolder>() {

    private var oldPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusOrderViewHolder {
        val binding = LayoutStatusOrderItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return StatusOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StatusOrderViewHolder,
        position: Int
    ) {
        val item = listStatusOrder[position]
        holder.binding.apply {
            if (!item.isSelected) {
                cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.light_grey
                    )
                )
                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.black
                    )
                )
            } else {
                cardStatus.setCardBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.button_color
                    )
                )
                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.white
                    )
                )
            }
            tvStatus.text = item.statusName
            tvStatus.setOnClickListener {
                onStatusClick(item.statusName)
                if (oldPosition != position) {
                    if (oldPosition != -1) {
                        listStatusOrder[oldPosition].isSelected = false
                        notifyItemChanged(oldPosition)
                    }
                    oldPosition = holder.bindingAdapterPosition
                    listStatusOrder[position].isSelected = true
                    cardStatus.setCardBackgroundColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.button_color
                        )
                    )
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.white
                        )
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = listStatusOrder.size

    class StatusOrderViewHolder(val binding: LayoutStatusOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}