package com.example.shoppingapp.ui.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ProductReviewItem
import com.example.shoppingapp.domain.model.ProductRatingStats
import com.example.shoppingapp.domain.usecase.review_usecase.GetProductReviewsUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.GetProductRatingStatsUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductReviewsState(
    val items: List<ProductReviewItem> = emptyList(), val loading: Boolean = false,
    val error: Boolean = false, val endReached: Boolean = false
)

@HiltViewModel
class ProductReviewsViewModel @Inject constructor(
    private val getReviews: GetProductReviewsUseCase,
    private val getStats: GetProductRatingStatsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProductReviewsState())
    val state = _state.asStateFlow()
    private val _stats = MutableStateFlow<StateListener<ProductRatingStats>>(StateListener.Idle)
    val stats = _stats.asStateFlow()
    private var page = 0
    private var productId: Int? = null
    private var listJob: Job? = null
    private var statsJob: Job? = null

    fun refresh(id: Int, keepExisting: Boolean = false) {
        if (id <= 0 || (keepExisting && productId == id)) return
        listJob?.cancel()
        statsJob?.cancel()
        productId = id
        page = 0
        _state.value = ProductReviewsState()
        loadMore()
        statsJob = viewModelScope.launch {
            _stats.value = StateListener.Loading
            _stats.value = getStats(id)
        }
    }

    fun loadMore() {
        val id = productId ?: return
        if (_state.value.loading || _state.value.endReached) return
        _state.value = _state.value.copy(loading = true, error = false)
        listJob = viewModelScope.launch {
            when (val result = getReviews(id, page)) {
                is StateListener.Success -> {
                    _state.value = ProductReviewsState(
                        items = (_state.value.items + result.result.items).distinctBy { it.reviewId },
                        // Existing pageSize is the returned item count, not a configured page limit.
                        endReached = result.result.items.isEmpty()
                    )
                    page += 1
                }
                else -> _state.value = _state.value.copy(loading = false, error = true)
            }
        }
    }
}
