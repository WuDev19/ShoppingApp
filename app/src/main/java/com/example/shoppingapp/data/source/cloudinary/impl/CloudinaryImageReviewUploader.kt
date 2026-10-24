package com.example.shoppingapp.data.source.cloudinary.impl

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shoppingapp.R
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.data.source.review.ReviewImageReader
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryImageReviewUploader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reader: ReviewImageReader
) : CloudinaryUploader {
    override suspend fun upload(uri: Uri, position: Int?): String = try {
        withTimeout(StringConstants.REVIEW_UPLOAD_TIMEOUT_MS) { awaitUpload(uri) }
    } catch (e: TimeoutCancellationException) {
        throw IOException(context.getString(R.string.review_image_upload_error), e)
    }

    private suspend fun awaitUpload(uri: Uri): String {
        val payload = reader.read(uri)
        val hash = MessageDigest.getInstance("SHA-1").digest(payload.bytes).joinToString("") { "%02x".format(it) }
        return suspendCancellableCoroutine { continuation ->
            val completed = AtomicBoolean(false)
            fun fail() {
                if (completed.compareAndSet(false, true) && continuation.isActive)
                    continuation.resumeWithException(IOException(context.getString(R.string.review_image_upload_error)))
            }
            val requestId = MediaManager.get().upload(uri)
                .unsigned(StringConstants.UPLOAD_PRESET)
                .option(StringConstants.FOLDER_CLOUDINARY, StringConstants.FOLDER_REVIEW_IMAGE)
                .option(StringConstants.PUBLIC_ID, StringConstants.REVIEW_IMAGE_PUBLIC_ID_PREFIX + hash)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) = Unit
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) = Unit
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get(StringConstants.SECURE_URL_RESPONSE) as? String
                        if (url == null || !url.startsWith("https://")) { fail(); return }
                        if (completed.compareAndSet(false, true) && continuation.isActive) continuation.resume(url)
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) { fail() }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        // An offline WorkManager request must not leave the review form loading forever.
                        fail()
                        requestId?.let { MediaManager.get().cancelRequest(it) }
                    }
                }).dispatch()
            continuation.invokeOnCancellation {
                completed.set(true)
                MediaManager.get().cancelRequest(requestId)
            }
        }
    }
}
