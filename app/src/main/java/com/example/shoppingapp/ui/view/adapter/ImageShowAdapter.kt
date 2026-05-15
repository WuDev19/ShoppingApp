package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutImageShowItemBinding
import com.example.shoppingapp.domain.model.ReviewImage

class ImageShowAdapter(
    private var listReviewImage: List<ReviewImage>,
    private val callback: () -> Unit,
    private val delete: (ReviewImage) -> Unit
) :
    RecyclerView.Adapter<ImageShowAdapter.ImageShowViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateImages(listReviewImage: List<ReviewImage>) {
        this.listReviewImage = listReviewImage
        notifyDataSetChanged()
    }

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
                .error(R.drawable.icon_not_found)
                .placeholder(R.drawable.place_holder)
                .into(imgImage)
            imgImage.setOnClickListener {
                callback()
            }
            imgDelete.setOnClickListener {
                delete(item)
            }
        }
    }

    override fun getItemCount(): Int = listReviewImage.size

    class ImageShowViewHolder(val binding: LayoutImageShowItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}