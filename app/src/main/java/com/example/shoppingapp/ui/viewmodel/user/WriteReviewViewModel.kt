package com.example.shoppingapp.ui.viewmodel.user

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.CreateReview
import com.example.shoppingapp.domain.usecase.review_usecase.CreateReviewUseCase
import com.example.shoppingapp.domain.usecase.review_usecase.UploadImageReviewUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val createReviewUseCase: CreateReviewUseCase,
    private val uploadImageReviewUseCase: UploadImageReviewUseCase
) : ViewModel() {

    private val _postReviewState = Channel<StateListener<Unit>>()
    val postReviewState = _postReviewState.receiveAsFlow()

    // TODO: nao refractor upload ảnh thông qua backend sau
    @OptIn(ExperimentalCoroutinesApi::class)
    fun postReview(createReview: CreateReview, list: List<Uri>) {
        viewModelScope.launch {
            if (list.isEmpty()) {
                _postReviewState.send(
                    StateListener.Error(Exception(StringConstants.IMAGE_VIDEO_UPLOAD_EMPTY))
                )
                return@launch
            }
            _postReviewState.send(StateListener.Loading)
            try {
                val listUrl = list.asFlow()
                    .flatMapMerge(concurrency = 3) { uri ->
                        uploadWithRetry(uri)
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
                    .toList()
                if (listUrl.size != list.size) {
                    throw Exception(StringConstants.IMAGE_VIDEO_UPLOAD_ERROR)
                }
                createReview.urls = listUrl
                val result = createReviewUseCase(createReview)
                _postReviewState.send(result)
            } catch (e: Exception) {
                _postReviewState.send(StateListener.Error(e))
            }
        }
    }

    fun uploadWithRetry(uri: Uri): Flow<StateListener<String>> {
        return uploadImageReviewUseCase(uri)
            .retryWhen { cause, attempt ->
                if (attempt >= 3) return@retryWhen false
                val shouldRetry = cause is IOException // chỉ retry khi lỗi IO
                if (shouldRetry) {
                    delay((attempt + 1) * 1000L) // tạo trễ mỗi lần retry
                }
                shouldRetry
            }
    }
}