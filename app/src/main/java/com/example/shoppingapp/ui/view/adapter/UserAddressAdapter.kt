package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.UserAddressItemBinding
import com.example.shoppingapp.domain.model.AddressItem

class UserAddressAdapter :
    ListAdapter<AddressItem, UserAddressAdapter.UserAddressViewHolder>(diffCallBack) {
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
               tvName.text = it.name
               tvPhone.text = it.sdt
               tvAddress.text = it.addressName
               tvDefault.isVisible = it.defaultAddress
           }
       }
    }

    class UserAddressViewHolder(val binding: UserAddressItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}