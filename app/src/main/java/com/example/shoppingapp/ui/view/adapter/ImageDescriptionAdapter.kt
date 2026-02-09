package com.example.shoppingapp.ui.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.ImageDescriptionItemBinding

class ImageDescriptionAdapter(val callback: () -> Unit, val delete: (Uri) -> Unit) :
    ListAdapter<Uri, ImageDescriptionAdapter.ImageDescriptionViewHolder>(diffCallBack) {

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean = oldItem.toString() == newItem.toString()

            override fun areContentsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageDescriptionViewHolder {
        val binding =
            ImageDescriptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageDescriptionViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        item?.let {
            uri ->
            holder.binding.apply {
                Glide.with(holder.itemView)
                    .load(uri)
                    .error(R.drawable.place_holder)
                    .into(imgImage)
                imgImage.setOnClickListener {
                    callback()
                }
                imgDelete.setOnClickListener {
                    delete(uri)
                }
            }

        }
    }

    class ImageDescriptionViewHolder(val binding: ImageDescriptionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}