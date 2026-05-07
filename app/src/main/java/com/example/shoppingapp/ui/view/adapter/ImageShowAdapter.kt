package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutImageShowItemBinding
import com.example.shoppingapp.domain.model.ReviewImage

class ImageShowAdapter(val listReviewImage: List<ReviewImage>) :
    RecyclerView.Adapter<ImageShowAdapter.ImageShowViewHolder>() {

    var widthDevice = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageShowViewHolder {
        val binding = LayoutImageShowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        widthDevice = parent.context.resources.displayMetrics.widthPixels
        return ImageShowViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageShowViewHolder,
        position: Int
    ) {
        val item = listReviewImage[position]
        holder.binding.apply {
            Glide.with(root.context)
                .load(item.imageUrl)
                .override(widthDevice * 33 / 100, 150)
                .error(R.drawable.icon_not_found)
                .placeholder(R.drawable.place_holder)
                .into(imgImage)
        }
    }

    override fun getItemCount(): Int = listReviewImage.size

    class ImageShowViewHolder(val binding: LayoutImageShowItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}