package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutPurchaseProductBinding
import com.example.shoppingapp.domain.model.AddressItem

class MainPurchaseAdapter(
    private val orderProductAdapter: OrderProductAdapter,
    private val onAddressClick: () -> Unit
) : RecyclerView.Adapter<MainPurchaseAdapter.MainPurchaseViewHolder>() {
    private var address: AddressItem? = null
    private var totalPrice: Int? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setAddress(addressItem: AddressItem) {
        address = addressItem
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTotalPrice(totalPrice: Int){
        this.totalPrice = totalPrice
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainPurchaseViewHolder {
        val binding = LayoutPurchaseProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainPurchaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MainPurchaseViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            linearAddress.setOnClickListener {
                onAddressClick()
            }
            tvAddress.text = address?.addressName ?: ""
            tvUsername.text = address?.name ?: ""
            tvPhone.text = address?.sdt ?: ""
            tvTotalPrice.text = buildString {
                append(totalPrice)
                append("$")
            }
            recyclerViewOrder.apply {
                adapter = orderProductAdapter
                layoutManager = LinearLayoutManager(context)
            }
            radioCod.setOnClickListener {
                radioCod.isChecked = true
                radioOlb.isChecked = false
            }
            radioOlb.setOnClickListener {
                radioOlb.isChecked = true
                radioCod.isChecked = false
            }
        }
    }

    override fun getItemCount(): Int = 1

    class MainPurchaseViewHolder(val binding: LayoutPurchaseProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}