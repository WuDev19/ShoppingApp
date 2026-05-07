package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.databinding.ProductItemInHomeBinding
import com.example.shoppingapp.domain.model.product.ProductDisplay

class ProductAdapter(private val onItemClick: (productId: Int, sellerId: Int) -> Unit) :
    PagingDataAdapter<ProductDisplay, ProductAdapter.ProductHomeViewHolder>(DiffCallBack) {

    class ProductHomeViewHolder(val binding: ProductItemInHomeBinding) :
        ViewHolder(binding.root)

    companion object {

        val DiffCallBack = object : DiffUtil.ItemCallback<ProductDisplay>() {
            override fun areItemsTheSame(
                oldItem: ProductDisplay,
                newItem: ProductDisplay
            ): Boolean {
                return oldItem.maSp == newItem.maSp
            }

            override fun areContentsTheSame(
                oldItem: ProductDisplay,
                newItem: ProductDisplay
            ): Boolean {
                return oldItem == newItem
            }
        }

        const val PRODUCT_TYPE = 103
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHomeViewHolder {
        val binding = ProductItemInHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductHomeViewHolder, position: Int) {
        val product = getItem(position) ?: return
        holder.binding.apply {
            tvDiscount.text = product.giamgia.toString()
            tvDescription.text = product.motaSp
            Glide.with(holder.itemView.context)
                .load(product.thumbnail)
                .placeholder(R.drawable.place_holder)
                .override(200, 200)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.icon_cpu)
                .into(imgThumbnail)
            tvNumberBought.text = buildString {
                append(StringConstants.SOLD)
                append(product.numberBought)
            }
            tvNameSeller.text = product.tenNguoiBan
            if (product.giamgia != 0) {
                tvDiscount.text = buildString {
                    append(product.giamgia.toString())
                    append(StringConstants.PERCENT)
                }
            } else {
                tvDiscount.visibility = View.GONE
            }
            tvCost.text = buildString {
                append(product.giaSp.toString())
                append(StringConstants.PRICE)
            }
            productItemMain.setOnClickListener {
                product.maSp?.let { it1 ->
                    product.maNguoiBan?.let { it2 ->
                        onItemClick(
                            it1,
                            it2
                        )
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return PRODUCT_TYPE
    }

}