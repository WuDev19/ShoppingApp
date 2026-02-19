package com.example.shoppingapp.utils

import android.net.Uri
import com.example.shoppingapp.domain.model.UploadingModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UploadingManager {
    private val _uploadPercent: MutableSharedFlow<UploadingModel> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val uploadPercent = _uploadPercent.asSharedFlow()

    fun pushPercent(position: Int, percent: Float?, uploadId: String?, uri: Uri) {
        _uploadPercent.tryEmit(UploadingModel(position, percent, uploadId, uri))
    }

}