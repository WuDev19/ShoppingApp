package com.example.shoppingapp.ui.view.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.databinding.HeaderDetailItemLayoutBinding
import com.example.shoppingapp.domain.model.ProductDetail
import java.util.stream.Collectors

class HeaderDetailAdapter(
    private val productDetail: ProductDetail,
    private val onItemClick: (List<String>) -> Unit
) : Adapter<HeaderDetailAdapter.HeaderDetailViewHolder>() {

    companion object {
        const val HEADER_TYPE = 100
    }

    class HeaderDetailViewHolder(val binding: HeaderDetailItemLayoutBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderDetailViewHolder {
        val binding = HeaderDetailItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: HeaderDetailViewHolder, position: Int) {
        if (productDetail.giamgia != 0) {
            val discountCost =
                productDetail.giaSp?.minus(((productDetail.giamgia?.let { it1 ->
                    productDetail.giaSp?.times(
                        it1
                    )
                })?.div(100)!!))
            holder.binding.tvCost.text = buildString {
                append(productDetail.giaSp.toString())
                append(StringConstants.PRICE)
            }
            holder.binding.tvCost.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvDiscount.text = buildString {
                append(productDetail.giamgia.toString())
                append(StringConstants.PERCENT)
            }
            holder.binding.tvCostWithDiscount.text = buildString {
                append(discountCost.toString())
                append(StringConstants.PRICE)
            }
        } else {
            holder.binding.tvDiscount.visibility = View.GONE
            holder.binding.tvCostWithDiscount.visibility = View.GONE
            holder.binding.tvCost.text = buildString {
                append(productDetail.giaSp.toString())
                append(StringConstants.PRICE)
            }
            holder.binding.tvCost.setTypeface(null, Typeface.BOLD)
        }
        if (productDetail.numberBought == 0) {
            holder.binding.tvNumberBought.visibility = View.GONE
        } else {
            holder.binding.tvNumberBought.text = productDetail.numberBought.toString()
        }
        holder.binding.tvDescription.text = productDetail.motaSp
        Glide.with(holder.itemView)
            .load(productDetail.avtShop)
            .error(R.drawable.icon_avatar)
            .into(holder.binding.imgAvtShop)
        holder.binding.tvTenShop.text = productDetail.tenNguoiBan
        holder.binding.tvNumberProducts.text = productDetail.totalProduct.toString()
        holder.binding.imgSlider.setImageList(
            productDetail.listAnhMoTa?.stream()?.map { it1 ->
                it1.toSlideModel()
            }?.collect(Collectors.toList()) ?: emptyList()
        )
        holder.binding.imgSlider.setItemClickListener(object : ItemClickListener{
            override fun doubleClick(position: Int) {}

            override fun onItemSelected(position: Int) {
                productDetail.listAnhMoTa?.let { it1 -> onItemClick(it1) }
            }

        })
    }

    override fun getItemViewType(position: Int): Int {
        return HEADER_TYPE
    }

    private fun String.toSlideModel(): SlideModel {
        return SlideModel(this, ScaleTypes.CENTER_CROP)
    }

}