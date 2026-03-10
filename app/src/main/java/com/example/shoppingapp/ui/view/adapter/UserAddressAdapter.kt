package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.UserAddressItemBinding
import com.example.shoppingapp.domain.model.AddressItem

class UserAddressAdapter(
    private val isFromPurchase: Boolean,
    private val onItemClick: (AddressItem) -> Unit,
    private val onSelectAddress: (AddressItem) -> Unit
) : ListAdapter<AddressItem, UserAddressAdapter.UserAddressViewHolder>(diffCallBack) {
    private var oldPosition = -1

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<AddressItem>() {
            override fun areItemsTheSame(
                oldItem: AddressItem,
                newItem: AddressItem
            ): Boolean {
                return oldItem.idAddress == newItem.idAddress
            }

            override fun areContentsTheSame(
                oldItem: AddressItem,
                newItem: AddressItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserAddressViewHolder {
        val binding = UserAddressItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserAddressViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: UserAddressViewHolder,
        position: Int
    ) {
        val userAddress = getItem(position)
        holder.binding.apply {
            userAddress?.let {
                if (userAddress.isSelected) {
                    cardUserAddress.strokeColor =
                        ContextCompat.getColor(root.context, R.color.button_color)
                    cardUserAddress.strokeWidth = 5
                } else {
                    cardUserAddress.strokeColor =
                        ContextCompat.getColor(root.context, R.color.black)
                    cardUserAddress.strokeWidth = 1
                }
                tvName.text = it.name
                tvPhone.text = it.sdt
                tvAddress.text = it.addressName
                tvDefault.isVisible = it.defaultAddress
                tvAddress.setOnClickListener { _ ->
                    onItemClick(it)
                }
                if (isFromPurchase) {
                    linearUserAddress.setOnClickListener { _ ->
                        onSelectAddress(it)
                        if (oldPosition != position) {
                            userAddress.isSelected = true
                            cardUserAddress.strokeColor =
                                ContextCompat.getColor(root.context, R.color.button_color)
                            cardUserAddress.strokeWidth = 5
                            if (oldPosition != -1) {
                                getItem(oldPosition).isSelected = false
                                notifyItemChanged(oldPosition)
                            }
                            oldPosition = holder.bindingAdapterPosition
                        }
                    }
                }
            }
        }
    }

    class UserAddressViewHolder(val binding: UserAddressItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}