package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.usecase.review_usecase.GetProductReviewDetailUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val getProductReviewDetailUseCase: GetProductReviewDetailUseCase
) : ViewModel() {


    private val _productReviewDetailState = Channel<StateListener<ProductReview>>()
    val productReviewDetailState = _productReviewDetailState.receiveAsFlow()

    fun getProductReviewDetail(reviewId: Int) {
        viewModelScope.launch {
            _productReviewDetailState.send(StateListener.Loading)
            val result = getProductReviewDetailUseCase(reviewId)
            _productReviewDetailState.send(result)
        }
    }

}