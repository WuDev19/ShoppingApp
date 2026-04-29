package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutStarItemBinding
import com.example.shoppingapp.domain.model.SelectedStar

class StarAdapter(private val listStar: List<SelectedStar>) :
    RecyclerView.Adapter<StarAdapter.StarViewHolder>() {
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
        val item = listStar[position]
        holder.binding.apply {
            if (item.selected) {
                imgStar.setImageResource(R.drawable.icon_star)
            } else {
                imgStar.setImageResource(R.drawable.icon_star_unchoose)
            }
            imgStar.setOnClickListener {
                if (item.selected && position + 1 < itemCount && listStar[position + 1].selected) {
                    for (i in itemCount - 1 downTo position) {
                        listStar[i].selected = false
                        notifyItemChanged(i)
                    }
                } else if (item.selected && position + 1 < itemCount && !listStar[position + 1].selected) {
                    for (i in 0..position) {
                        listStar[i].selected = false
                        notifyItemChanged(i)
                    }
                } else {
                    for (i in 0..position) {
                        listStar[i].selected = true
                        notifyItemChanged(i)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listStar.size

    class StarViewHolder(val binding: LayoutStarItemBinding) : RecyclerView.ViewHolder(binding.root)
}