package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.UploadImageItemBinding
import com.example.shoppingapp.domain.model.UploadingModel

class UploadImageAdapter(private val listUploadModel: MutableList<UploadingModel>) :
    RecyclerView.Adapter<UploadImageAdapter.UploadImageViewHolder>() {

    fun update(position: Int, uploadingModel: UploadingModel) {
        listUploadModel[position] = uploadingModel
        notifyItemChanged(position)
    }

    fun addItem(uploadingModel: UploadingModel){
        listUploadModel.add(uploadingModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadImageViewHolder {
        val binding = UploadImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UploadImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: UploadImageViewHolder,
        position: Int
    ) {
        val item = listUploadModel[position]
        holder.binding.apply {
            Glide.with(holder.binding.root)
                .load(item.uri)
                .error(R.drawable.icon_avatar)
                .into(imgImageProduct)
            progressBarUpload.progress = item.percent!!.toInt()
        }
    }

    override fun getItemCount(): Int = listUploadModel.size

    class UploadImageViewHolder(val binding: UploadImageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}