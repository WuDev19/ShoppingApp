package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.ShopNameItemInStoreBinding
import com.example.shoppingapp.domain.model.SellerInStore

class SellerInStoreAdapter(private val onItemClickListener: (Int) -> Unit) :
    PagingDataAdapter<SellerInStore, SellerInStoreAdapter.SellerInStoreViewHolder>(DIFFCALLBACK) {

    companion object DIFFCALLBACK : DiffUtil.ItemCallback<SellerInStore>() {
        override fun areItemsTheSame(oldItem: SellerInStore, newItem: SellerInStore): Boolean {
            return oldItem.maNguoiBan == newItem.maNguoiBan
        }

        override fun areContentsTheSame(oldItem: SellerInStore, newItem: SellerInStore): Boolean {
            return oldItem == newItem
        }

    }

    class SellerInStoreViewHolder(val binding: ShopNameItemInStoreBinding) :
        ViewHolder(binding.root)

    override fun onBindViewHolder(holder: SellerInStoreViewHolder, position: Int) {
        val seller = getItem(position)
        seller?.let {
            holder.binding.apply {
                Glide.with(holder.itemView)
                    .load(it.avtShop)
                    .error(R.drawable.icon_avatar)
                    .into(imgAvtShop)
                tvNumberProduct.text = it.quantityOnSale.toString()
                tvShopName.text = it.tenShop
                linearShop.setOnClickListener { _ ->
                    it.maNguoiBan?.let { it1 -> onItemClickListener(it1) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerInStoreViewHolder {
        val binding =
            ShopNameItemInStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SellerInStoreViewHolder(binding)
    }

}