package com.example.shoppingapp.data.source.cloudinary.impl

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.utils.FileHashUtils
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryImageReviewUploader @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CloudinaryUploader {

    override suspend fun upload(uri: Uri, position: Int?): String =
        suspendCancellableCoroutine { continuation ->
            val publicId = "reviews/${FileHashUtils.sha1(context, uri)}"
            val requestId = MediaManager.get()
                .upload(uri)
                .unsigned(StringConstants.UPLOAD_PRESET)
                .option(StringConstants.FOLDER_CLOUDINARY, StringConstants.FOLDER_REVIEW_IMAGE)
                .option(StringConstants.PUBLIC_ID, publicId)
                .option(StringConstants.OVERRIDE_CLOUDINARY, true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                    }

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: Map<*, *>?
                    ) {
                        val url = resultData?.get(StringConstants.SECURE_URL_RESPONSE) as? String
                            ?: return continuation.resumeWithException(
                                IllegalStateException(StringConstants.URL_SECURE_MISSING)
                            )
                        if (!continuation.isActive) return
                        continuation.resume(url)
                    }

                    override fun onError(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                        if (!continuation.isActive) return
                        continuation.resumeWithException(RuntimeException(error?.description))
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                    }

                })
                .dispatch()
            continuation.invokeOnCancellation {
                requestId?.let {
                    MediaManager.get().cancelRequest(requestId)
                }
            }
        }
}