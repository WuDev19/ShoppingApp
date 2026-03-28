package com.example.shoppingapp.ui.view.adapter

import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutProductOrderSameShopBinding
import com.example.shoppingapp.databinding.OrderLayoutBinding
import com.example.shoppingapp.databinding.OrderOfShopLayoutBinding
import com.example.shoppingapp.databinding.OrderReviewButtonBinding
import com.example.shoppingapp.domain.model.OrderUiModel

class OrderViaStatusAdapter :
    PagingDataAdapter<OrderUiModel, RecyclerView.ViewHolder>(diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<OrderUiModel>() {
            override fun areItemsTheSame(
                oldItem: OrderUiModel,
                newItem: OrderUiModel
            ): Boolean {
                return when {
                    oldItem is OrderUiModel.OrderOfShop &&
                            newItem is OrderUiModel.OrderOfShop -> {
                        oldItem.shopId == newItem.shopId
                    }

                    oldItem is OrderUiModel.OrderItemUser &&
                            newItem is OrderUiModel.OrderItemUser -> {
                        oldItem.order.orderId == newItem.order.orderId &&
                                oldItem.orderItemDetail.orderItemId == newItem.orderItemDetail.orderItemId
                    }

                    oldItem is OrderUiModel.OrderReview &&
                            newItem is OrderUiModel.OrderReview -> {
                        oldItem.orderId == newItem.orderId
                    }

                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: OrderUiModel,
                newItem: OrderUiModel
            ): Boolean = oldItem == newItem
        }

        const val ORDER_OF_SHOP: Int = 1
        const val ORDER = 2
        const val ORDER_ITEM: Int = 3
        const val ORDER_REVIEW: Int = 4


    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is OrderUiModel.OrderOfShop -> ORDER_OF_SHOP
            is OrderUiModel.OrderItemUser -> ORDER_ITEM
            is OrderUiModel.Order -> ORDER
            else -> ORDER_REVIEW
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ORDER -> {
                val binding = OrderLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OrderViewHolder(binding)
            }

            ORDER_ITEM -> {
                val binding = LayoutProductOrderSameShopBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OrderItemViewHolder(binding)
            }

            ORDER_OF_SHOP -> {
                val binding = OrderOfShopLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OrderOfShopViewHolder(binding)
            }

            else -> {
                val binding = OrderReviewButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OrderReviewButtonViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val density = holder.itemView.context.resources.displayMetrics.density
        when (val item = getItem(position)) {
            is OrderUiModel.Order -> {
                (holder as OrderViewHolder).bind(item)
            }

            is OrderUiModel.OrderItemUser -> {
                (holder as OrderItemViewHolder).bind(item)
                holder.binding.apply {
                    val params = linearOrderItem.layoutParams as RecyclerView.LayoutParams
                    params.setMargins(
                        (10 * density).toInt(),
                        (10 * density).toInt(),
                        (10 * density).toInt(),
                        0
                    )
                }
            }

            is OrderUiModel.OrderReview -> {
                (holder as OrderReviewButtonViewHolder).bind(item)
            }

            is OrderUiModel.OrderOfShop -> {
                (holder as OrderOfShopViewHolder).bind(item)
                holder.binding.apply {
                    val params = cardShop.layoutParams as RecyclerView.LayoutParams
                    if (position != 0) {
                        params.topMargin = (50 * density).toInt()
                        cardShop.layoutParams = params
                    } else {
                        params.topMargin = (10 * density).toInt()
                        cardShop.layoutParams = params
                    }
                }
            }

            else -> Unit
        }
    }

    class OrderViewHolder(val binding: OrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderUiModel.Order) {
            binding.apply {
                tvStatus.text = order.orderStatus
            }
        }

    }

    class OrderItemViewHolder(val binding: LayoutProductOrderSameShopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItemUser: OrderUiModel.OrderItemUser) {
            val orderItem = orderItemUser.orderItemDetail
            binding.apply {
                Glide.with(root.context)
                    .load(orderItem.detailProduct.anhDetail)
                    .error(R.drawable.icon_not_found)
                    .into(imgAvatar)
                tvProductName.text = orderItem.detailProduct.tenDetail
                tvColorSize.text = buildString {
                    append(orderItem.detailProduct.color)
                    append(", ")
                    append(orderItem.detailProduct.kichco)
                }
                tvPrice.text = buildString {
                    append(orderItem.detailProduct.unitPrice)
                    append("$")
                }
                tvPrice.paintFlags = TextPaint.STRIKE_THRU_TEXT_FLAG
                tvPriceDCount.text = buildString {
                    append(orderItem.price)
                    append("$")
                }
                tvNumber.text = buildString {
                    append("x")
                    append(orderItem.number.toString())
                }
            }
        }
    }

    class OrderReviewButtonViewHolder(val binding: OrderReviewButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderReview: OrderUiModel.OrderReview) {
            binding.apply {
                if (orderReview.orderStatus == "Completed") {
                    btnReview.isVisible = true
                } else {
                    btnReview.isVisible = false
                }
            }
        }
    }

    class OrderOfShopViewHolder(val binding: OrderOfShopLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderOfShop: OrderUiModel.OrderOfShop) {
            binding.apply {
                tvShopName.text = orderOfShop.shopName
                Glide.with(root.context)
                    .load(orderOfShop.shopAvt)
                    .error(R.drawable.icon_avatar)
                    .into(imgShopAvt)
            }
        }
    }
}