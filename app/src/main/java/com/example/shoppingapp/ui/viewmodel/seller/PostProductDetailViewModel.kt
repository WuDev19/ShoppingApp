package com.example.shoppingapp.ui.viewmodel.seller

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.TokenManager
import com.example.shoppingapp.utils.UploadingManager
import com.example.shoppingapp.data.remote.dto.request.ImageDescriptionProduct
import com.example.shoppingapp.data.remote.dto.request.PostProductDetailRequest
import com.example.shoppingapp.data.remote.dto.request.PostProductRequest
import com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryImageProductUploader
import com.example.shoppingapp.domain.model.UploadingModel
import com.example.shoppingapp.domain.usecase.productusecase.post.PostProductUseCase
import com.example.shoppingapp.domain.usecase.productusecase.post.UploadImageProductUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostProductDetailViewModel @Inject constructor(
    private val uploader: CloudinaryImageProductUploader,
    private val postProductUseCase: PostProductUseCase,
    private val uploadImageProductUseCase: UploadImageProductUseCase,
    val uploadingManager: UploadingManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _postProductState: MutableSharedFlow<StateListener<Int>> = MutableSharedFlow()
    val postProductState = _postProductState.asSharedFlow()

    private val _numberImageSuccess: MutableStateFlow<Int> = MutableStateFlow(0)
    val numberImageSuccess = _numberImageSuccess.asStateFlow()

    private val _uploadImageDetailState: MutableSharedFlow<StateListener<PostProductDetailRequest>> =
        MutableSharedFlow()
    val uploadImageDetailState = _uploadImageDetailState.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun postProduct(
        listUri: List<Uri>,
        postProductRequest: PostProductRequest
    ) {
        viewModelScope.launch {
            _postProductState.emit(StateListener.Loading)
            try {
                val listImagesGeneral = listUri.asFlow()
                    //cho chạy đồng thời 5 cái cùng lúc
                    .flatMapMerge(5) { uri ->
                        flow {
                            val result =
                                runCatching { uploader.upload(uri) }.getOrNull() //runCatching có nhiệm vụ tránh cho exception làm crash
                            Log.e(
                                StringConstants.DEBUG,
                                "postProduct: ${Thread.currentThread().name}"
                            )
                            emit(result)
                        }.flowOn(Dispatchers.IO)
                    }
                    .filterNotNull() //lọc nhưng result khác null
                    .onEach {
                        _numberImageSuccess.value += 1
                    }
                    .toList() //toList là suspend function

                if (listImagesGeneral.isNotEmpty() && listImagesGeneral.size == listUri.size) {
                    val imageDescriptionProducts = listImagesGeneral.map {
                        ImageDescriptionProduct(it)
                    }
                    postProductRequest.imageDescriptionProducts = imageDescriptionProducts
                    postProductRequest.thumbnail = imageDescriptionProducts.first().url
                    val result = postProductUseCase(tokenManager.getSellerId(), postProductRequest)
                    _postProductState.emit(StateListener.Success(result))
                } else {
                    _postProductState.emit(StateListener.Error(Exception("Image upload failed, please try again")))
                }
            } catch (e: Exception) {
                _postProductState.emit(StateListener.Error(e))
            } finally {
                _numberImageSuccess.value = 0
            }
        }
    }

    fun uploadImageProductDetail(
        uri: Uri,
        postProductDetailRequest: PostProductDetailRequest,
        uploadingModel: UploadingModel
    ) {
        viewModelScope.launch {
            _uploadImageDetailState.emit(StateListener.Loading)
            try {
                val url = uploadImageProductUseCase(uri, uploadingModel.position)
                postProductDetailRequest.imageDetail = url
                _uploadImageDetailState.emit(StateListener.Success(postProductDetailRequest))
            } catch (e: Exception) {
                _uploadImageDetailState.emit(StateListener.Error(e))
            }
        }
    }

}