package com.example.shoppingapp.ui.viewmodel.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ProductReview
import com.example.shoppingapp.domain.model.UpdateReview
import com.example.shoppingapp.domain.usecase.review_usecase.GetProductReviewDetailUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.UpdateReviewUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.UploadImageReviewUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val getProductReviewDetailUseCase: GetProductReviewDetailUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val uploadImageReviewUseCase: UploadImageReviewUseCase
) : ViewModel() {

    private val _productReviewDetailState = Channel<StateListener<ProductReview>>()
    val productReviewDetailState = _productReviewDetailState.receiveAsFlow()
    private val _updateReviewState = Channel<StateListener<String>>()
    val updateReviewState = _updateReviewState.receiveAsFlow()

    fun getProductReviewDetail(reviewId: Int) {
        viewModelScope.launch {
            _productReviewDetailState.send(StateListener.Loading)
            val result = getProductReviewDetailUseCase(reviewId)
            _productReviewDetailState.send(result)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateReview(
        reviewId: Int?,
        comment: String,
        star: Int,
        listUri: List<Uri>,
        deleteImages: List<Int>
    ) {
        reviewId?.let { revId ->
            viewModelScope.launch {
                try {
                    _updateReviewState.send(StateListener.Loading)
                    val listUrl = mutableListOf<String>()
                    if (!listUri.isEmpty()) {
                        //upload ảnh trước
                        listUri.asFlow()
                            .flatMapMerge(concurrency = 3) { uri ->
                                uploadImageReviewUseCase(uri)
                            }
                            .map { state ->
                                when (state) {
                                    is StateListener.Success -> state.result
                                    is StateListener.Error -> throw state.e
                                        ?: Exception("Upload failed")

                                    else -> null
                                }
                            }
                            .filterNotNull()
                            .collect { url ->
                                listUrl.add(url)
                            }
                        if (listUrl.size != listUri.size) {
                            throw Exception(StringConstants.IMAGE_VIDEO_UPLOAD_ERROR)
                        }
                    }
                    val updateReview = UpdateReview(comment, star, deleteImages, listUrl)
                    val result = updateReviewUseCase(revId, updateReview)
                    _updateReviewState.send(result)
                } catch (e: Exception) {
                    _updateReviewState.send(StateListener.Error(e))
                }
            }
        }

    }


}