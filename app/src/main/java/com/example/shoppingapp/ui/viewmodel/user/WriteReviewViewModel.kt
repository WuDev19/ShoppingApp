package com.example.shoppingapp.ui.viewmodel.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.usecase.review_usecase.CreateReviewUseCase
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
class WriteReviewViewModel @Inject constructor(
    private val createReviewUseCase: CreateReviewUseCase,
    private val uploadImageReviewUseCase: UploadImageReviewUseCase
) : ViewModel() {
    private val _postReviewState = MutableStateFlow<StateListener<Unit>>(StateListener.Idle)
    val postReviewState = _postReviewState.asStateFlow()
    // Keep successfully uploaded images for an explicit retry after an API/network failure.
    private val uploaded = mutableMapOf<Uri, String>()

    fun postReview(createReview: CreateReview, images: List<Uri>) {
        if (_postReviewState.value is StateListener.Loading) return
        _postReviewState.value = StateListener.Loading
        val snapshot = images.toList()
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
                _postReviewState.value = createReviewUseCase(createReview.copy(urls = urls))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _postReviewState.value = StateListener.Error(e)
            }
        }
    }
}
