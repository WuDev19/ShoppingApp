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
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.databinding.OtherProductOfShopItemBinding
import com.example.shoppingapp.domain.model.Product

class OtherProductOfShopAdapter(private val onItemClick: (productId: Int, sellerId: Int) -> Unit) :
    PagingDataAdapter<Product, OtherProductOfShopAdapter.OtherProductOfShopViewHolder>(diffCallback) {

    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.maSp == newItem.maSp
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }

        const val OTHER_PRODUCT_TYPE = 102

    }

    class OtherProductOfShopViewHolder(val binding: OtherProductOfShopItemBinding) :
        ViewHolder(binding.root)

    override fun onBindViewHolder(holder: OtherProductOfShopViewHolder, position: Int) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherProductOfShopViewHolder {
        val binding = OtherProductOfShopItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OtherProductOfShopViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return OTHER_PRODUCT_TYPE
    }
}