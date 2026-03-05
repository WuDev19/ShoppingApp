package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutShippingMethodBinding
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn
import com.example.shoppingapp.domain.model.ghn.ShippingMethod

class ShippingMethodAdapter :
    ListAdapter<ShippingMethod, ShippingMethodAdapter.ShippingMethodViewHolder>(diffCallback) {

    private var oldPosition = 0

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ShippingMethod>() {
            override fun areItemsTheSame(
                oldItem: ShippingMethod,
                newItem: ShippingMethod
            ): Boolean = oldItem.shopId == newItem.shopId

            override fun areContentsTheSame(
                oldItem: ShippingMethod,
                newItem: ShippingMethod
            ): Boolean = oldItem == newItem
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShippingMethodViewHolder {
        val binding = LayoutShippingMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShippingMethodViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShippingMethodViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.apply {
            if (item.isSelected) {
                cardTime.strokeColor = ContextCompat.getColor(root.context, R.color.button_color)
            } else {
                cardTime.strokeColor = ContextCompat.getColor(root.context, R.color.light_grey)
            }
            if (position == 0) {
                cardTime.strokeColor = ContextCompat.getColor(root.context, R.color.button_color)
                item.isSelected = true
            }
            tvCost.text = buildString {
                append(item.feeServiceGhn.totalFee.toString())
                append("$")
            }
            tvTime.text = buildString {
                append("Receive from ")
                append(item.estimateGhn.from)
                append(" to ")
                append(item.estimateGhn.to)
            }
            tvServiceName.text =
                if (item.feeServiceGhn.serviceName == "Hàng nhẹ") "Express" else "Standard"
            cardTime.setOnClickListener {
                if (oldPosition != position) {
                    cardTime.strokeColor =
                        ContextCompat.getColor(root.context, R.color.button_color)
                    val oldItem = getItem(oldPosition)
                    oldItem.isSelected = false
                    notifyItemChanged(oldPosition)
                    oldPosition = holder.bindingAdapterPosition
                }
            }
        }
    }

    class ShippingMethodViewHolder(val binding: LayoutShippingMethodBinding) :
        RecyclerView.ViewHolder(binding.root)

}