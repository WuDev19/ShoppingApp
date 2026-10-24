package com.example.shoppingapp.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.ItemProductReviewBinding
import com.example.shoppingapp.databinding.ItemReviewSummaryBinding
import com.example.shoppingapp.databinding.ItemReviewFooterBinding
import com.example.shoppingapp.domain.model.ProductRatingStats
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.product.ProductReviewsState
import java.text.DateFormat
import java.util.Date

class ProductReviewsAdapter(
    private val loadMore: () -> Unit,
    private val refresh: () -> Unit,
    private val showImages: (List<String>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var state = ProductReviewsState()
    private var stats: StateListener<ProductRatingStats> = StateListener.Idle

    fun render(value: ProductReviewsState) {
        val previous = state.items
        state = value
        if (previous != value.items) {
            if (value.items.size >= previous.size && value.items.take(previous.size) == previous) {
                notifyItemRangeInserted(previous.size + 1, value.items.size - previous.size)
            } else {
                notifyItemRangeRemoved(1, previous.size)
                notifyItemRangeInserted(1, value.items.size)
            }
        }
        notifyItemChanged(itemCount - 1)
    }
    fun renderStats(value: StateListener<ProductRatingStats>) {
        stats = value
        notifyItemChanged(0)
    }
    override fun getItemCount() = state.items.size + 2
    override fun getItemViewType(position: Int) = when (position) {
        0 -> 0
        itemCount - 1 -> 2
        else -> 1
    }
    class Header(val binding: ItemReviewSummaryBinding) : RecyclerView.ViewHolder(binding.root)
    class Item(val binding: ItemProductReviewBinding) : RecyclerView.ViewHolder(binding.root)
    class Footer(val binding: ItemReviewFooterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> Header(ItemReviewSummaryBinding.inflate(inflater, parent, false))
            1 -> Item(ItemProductReviewBinding.inflate(inflater, parent, false))
            else -> Footer(ItemReviewFooterBinding.inflate(inflater, parent, false))
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Header -> holder.binding.apply {
                val value = (stats as? StateListener.Success)?.result
                reviewStats.text = when {
                    value != null -> root.context.getString(R.string.review_stats,
                        value.averageRating, value.totalReviews, value.fiveStar, value.fourStar,
                        value.threeStar, value.twoStar, value.oneStar)
                    stats is StateListener.Error -> root.context.getString(R.string.review_stats_error)
                    else -> root.context.getString(R.string.review_loading)
                }
                retryStats.isVisible = stats is StateListener.Error
                retryStats.setOnClickListener { refresh() }
            }
            is Item -> holder.binding.apply {
                val item = state.items[position - 1]
                reviewUser.text = item.userName.ifBlank { root.context.getString(R.string.review_buyer) }
                reviewRating.rating = item.rating.toFloat()
                reviewComment.text = item.comment
                reviewComment.isVisible = item.comment.isNotEmpty()
                reviewDate.text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(if (item.updatedAt > 0) item.updatedAt else item.createdAt))
                Glide.with(root).load(item.userAvatar).placeholder(R.drawable.place_holder).into(reviewAvatar)
                reviewImages.removeAllViews()
                item.images.forEach { image ->
                    val thumbnail = android.widget.ImageView(root.context)
                    val size = (72 * root.resources.displayMetrics.density).toInt()
                    val params = android.widget.LinearLayout.LayoutParams(size, size)
                    params.marginEnd = (8 * root.resources.displayMetrics.density).toInt()
                    thumbnail.layoutParams = params
                    thumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    thumbnail.contentDescription = root.context.getString(R.string.review_image)
                    Glide.with(root).load(image.imageUrl).override(size, size).into(thumbnail)
                    thumbnail.setOnClickListener { showImages(item.images.map { it.imageUrl }) }
                    reviewImages.addView(thumbnail)
                }
            }
            is Footer -> holder.binding.apply {
                reviewProgress.isVisible = state.loading
                reviewMessage.isVisible = state.error || (state.items.isEmpty() && !state.loading)
                reviewMessage.setText(if (state.error) R.string.review_load_error else R.string.review_empty)
                reviewMore.isVisible = !state.loading && (!state.endReached || state.error)
                reviewMore.setText(if (state.error) R.string.review_retry else R.string.review_more)
                reviewMore.setOnClickListener { loadMore() }
            }
        }
    }
}
