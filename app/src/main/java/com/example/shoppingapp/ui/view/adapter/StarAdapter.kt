package com.example.shoppingapp.ui.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutStarItemBinding
import com.example.shoppingapp.domain.model.SelectedStar

class StarAdapter(
    private var listStar: List<SelectedStar>,
    private val onChooseStar: (Int) -> Unit
) : RecyclerView.Adapter<StarAdapter.StarViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateStarFromServer(newListStar: List<SelectedStar>){
        this.listStar = newListStar
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StarViewHolder {
        val binding = LayoutStarItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StarViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StarViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            if (listStar[position].selected) {
                imgStar.setImageResource(R.drawable.icon_star)
            } else {
                imgStar.setImageResource(R.drawable.icon_star_unchoose)
            }
            imgStar.setOnClickListener {
                listStar.forEachIndexed { index, selectedStar ->
                    selectedStar.selected = index <= position
                }
                notifyItemRangeChanged(0, itemCount)
                onChooseStar(position + 1)
            }
        }
    }

    override fun getItemCount(): Int = listStar.size

    class StarViewHolder(val binding: LayoutStarItemBinding) : RecyclerView.ViewHolder(binding.root)
}