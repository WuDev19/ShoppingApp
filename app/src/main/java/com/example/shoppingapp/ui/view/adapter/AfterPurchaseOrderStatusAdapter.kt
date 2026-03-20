package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutOrderStatusResultBinding
import com.example.shoppingapp.domain.model.OrderStatus

class AfterPurchaseOrderStatusAdapter(
    private var listStatusOrder: List<OrderStatus>,
    private val onViewDetail: (String) -> Unit
) : RecyclerView.Adapter<AfterPurchaseOrderStatusAdapter.AfterPurchaseOrderStatusViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setListStatusOrder(listStatusOrder: List<OrderStatus>) {
        this.listStatusOrder = listStatusOrder
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AfterPurchaseOrderStatusViewHolder {
        val binding = LayoutOrderStatusResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AfterPurchaseOrderStatusViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AfterPurchaseOrderStatusViewHolder,
        position: Int
    ) {
        val item = listStatusOrder[position]
        holder.binding.apply {
            tvStatus.text = item.orderStatus
            tvOrderId.text = item.orderId
            tvOrderDate.text = item.orderDate
            tvExpectedDate.text = item.orderExpectedDate
            btDetailOrder.setOnClickListener {
                onViewDetail(item.orderId)
            }
        }
    }

    override fun getItemCount(): Int = listStatusOrder.size

    class AfterPurchaseOrderStatusViewHolder(val binding: LayoutOrderStatusResultBinding) :
        RecyclerView.ViewHolder(binding.root)

}