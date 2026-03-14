package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.databinding.LayoutSelectVoucherItemBinding
import com.example.shoppingapp.domain.model.ShopVouchers

class ShopVoucherAdapter(
    private var listVoucher: List<ShopVouchers>,
    private val onSelectVoucher: (Int, String, Int, Int, Int, String) -> Unit
) :
    RecyclerView.Adapter<ShopVoucherAdapter.ShopVoucherViewHolder>() {

    private var oldPosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun setVouchers(listVoucher: List<ShopVouchers>) {
        this.listVoucher = listVoucher
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopVoucherViewHolder {
        val binding = LayoutSelectVoucherItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShopVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShopVoucherViewHolder,
        position: Int
    ) {
        val voucher = listVoucher[position]
        holder.binding.apply {
            var voucherValue = ""
            rbSelect.isChecked = voucher.isSelected
            if (voucher.voucherType == "Percent") {
                voucherValue = buildString {
                    append(voucher.voucherValue)
                    append("%")
                    append(" ")
                    append("max")
                    append(" ")
                    append(voucher.maxDiscountValue)
                    append("$")
                }
                tvVoucherValue.text = voucherValue
            } else {
                voucherValue = buildString {
                    append("Discount ")
                    append(voucher.voucherValue)
                    append("$")
                }
                tvVoucherValue.text = buildString {
                    append(voucher.voucherValue.toString())
                    append("$")
                }
            }
            tvMinCost.text = buildString {
                append("Minimum order")
                append(" ")
                append(voucher.minOrderCost)
                append("$")
            }
            tvExpireTime.text = buildString {
                append("Expire ")
                append(voucher.endTime)
            }
            progressUsage.progress = voucher.usageCount * 100 / voucher.usageLimit
            rbSelect.setOnClickListener {
                if (oldPosition != position) {
                    voucher.isSelected = true
                    rbSelect.isChecked = true
                    if (oldPosition != -1) {
                        listVoucher[oldPosition].isSelected = false
                        notifyItemChanged(oldPosition)
                    }
                    oldPosition = holder.bindingAdapterPosition
                    onSelectVoucher(
                        voucher.voucherId,
                        voucherValue,
                        voucher.shopGhnId,
                        voucher.voucherValue,
                        voucher.maxDiscountValue,
                        voucher.voucherType
                    )
                } else {
                    if (!voucher.isSelected) {
                        voucher.isSelected = true
                        rbSelect.isChecked = true
                        onSelectVoucher(
                            voucher.voucherId,
                            voucherValue,
                            voucher.shopGhnId,
                            voucher.voucherValue,
                            voucher.maxDiscountValue,
                            voucher.voucherType
                        )
                    } else {
                        voucher.isSelected = false
                        rbSelect.isChecked = false
                        onSelectVoucher(-1, "", -1, -1, -1, "")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listVoucher.size

    class ShopVoucherViewHolder(val binding: LayoutSelectVoucherItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}