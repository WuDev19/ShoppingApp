package com.example.shoppingapp.ui.view.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.TiltleDetailItemLayoutBinding

class TitleDetailAdapter(
    private val rating: String,
    private val title: String,
    private val all: String,
    private val productId: Int?,
    private val onItemClick: ((Int) -> Unit)?
) : Adapter<TitleDetailAdapter.TitleDetailViewHolder>() {

    companion object {
        const val TITLE_TYPE = 101
    }

    class TitleDetailViewHolder(val binding: TiltleDetailItemLayoutBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleDetailViewHolder {
        val binding = TiltleDetailItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TitleDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: TitleDetailViewHolder, position: Int) {

        if(rating.isEmpty()){
            holder.binding.tvRatingReview.visibility = View.GONE
        }
        else{
            holder.binding.tvRatingReview.text = rating
        }

        if(all.isEmpty()){
            holder.binding.tvAll.visibility = View.GONE
        }
        else{
            holder.binding.tvAll.text = all
            holder.binding.tvAll.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        }

        holder.binding.tvTitle.text = title

        holder.binding.tvAll.setOnClickListener {
            onItemClick?.let { it1 -> productId?.let { it2 -> it1(it2) } }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return TITLE_TYPE
    }

}