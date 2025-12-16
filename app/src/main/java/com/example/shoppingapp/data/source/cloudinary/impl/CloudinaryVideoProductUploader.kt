package com.example.shoppingapp.data.source.cloudinary.impl

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import javax.inject.Inject
import kotlin.collections.get
class CloudinaryVideoProductUploader @Inject constructor() : CloudinaryUploader {
    override fun upload(
        uri: Uri,
        onProgress: (Int) -> Unit,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        MediaManager.get().upload(uri)
            .unsigned(StringConstants.UPLOAD_PRESET)
            .option(StringConstants.FOLDER_CLOUDINARY, StringConstants.FOLDER_PRODUCT_VIDEO)
            .option(StringConstants.RESOURCE_TYPE, StringConstants.TYPE_VIDEO)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {
                    onProgress(((bytes * 100) / totalBytes).toInt())
                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: Map<*, *>?
                ) {
                    resultData?.let {
                        onSuccess(it[StringConstants.SECURE_URL_RESPONSE] as String)
                    }
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    error?.let {
                        onFailure(it.description)
                    }
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                }

            }).dispatch()
    }
}