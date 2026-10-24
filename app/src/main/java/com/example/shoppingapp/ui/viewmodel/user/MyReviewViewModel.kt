package com.example.shoppingapp.ui.viewmodel.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.UpdateReview
import com.example.shoppingapp.domain.usecase.review_usecase.DeleteReviewUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.GetProductReviewDetailUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.UpdateReviewUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.UploadImageReviewUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val getProductReviewDetailUseCase: GetProductReviewDetailUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val uploadImageReviewUseCase: UploadImageReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) : ViewModel() {
    private val _productReviewDetailState = MutableStateFlow<StateListener<ProductReview>>(StateListener.Idle)
    val productReviewDetailState = _productReviewDetailState.asStateFlow()
    private val _updateReviewState = MutableStateFlow<StateListener<String>>(StateListener.Idle)
    val updateReviewState = _updateReviewState.asStateFlow()
    private val _deleteReviewState = MutableStateFlow<StateListener<String>>(StateListener.Idle)
    val deleteReviewState = _deleteReviewState.asStateFlow()
    private val uploaded = mutableMapOf<Uri, String>()
    val busy: Boolean get() = _updateReviewState.value is StateListener.Loading || _deleteReviewState.value is StateListener.Loading

    fun getProductReviewDetail(reviewId: Int) {
        if (_productReviewDetailState.value is StateListener.Loading || _productReviewDetailState.value is StateListener.Success) return
        _productReviewDetailState.value = StateListener.Loading
        viewModelScope.launch {
            _productReviewDetailState.value = getProductReviewDetailUseCase(reviewId)
        }
    }

    fun updateReview(reviewId: Int, comment: String, star: Int, listUri: List<Uri>, deleteImages: List<Int>) {
        if (busy) return
        _updateReviewState.value = StateListener.Loading
        val snapshot = listUri.toList()
        val deleting = deleteImages.toList()
        viewModelScope.launch {
            try {
                val urls = snapshot.map { uri ->
                    uploaded[uri] ?: when (val result = uploadImageReviewUseCase(uri).first {
                        it is StateListener.Success || it is StateListener.Error
                    }) {
                        is StateListener.Success -> result.result.also { uploaded[uri] = it }
                        is StateListener.Error -> throw result.e ?: IllegalStateException("Image upload failed")
                        else -> error("Image upload failed")
                    }
                }
                _updateReviewState.value = updateReviewUseCase(reviewId, UpdateReview(comment, star, deleting, urls))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _updateReviewState.value = StateListener.Error(e)
            }
        }
    }

    fun deleteReview(reviewId: Int) {
        if (busy) return
        _deleteReviewState.value = StateListener.Loading
        viewModelScope.launch {
            _deleteReviewState.value = deleteReviewUseCase(reviewId)
        }
    }
}
