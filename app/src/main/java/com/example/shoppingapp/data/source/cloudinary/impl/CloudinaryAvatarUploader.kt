package com.example.shoppingapp.data.source.cloudinary.impl

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.collections.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//suspendCancellableCoroutine nghĩa là coroutine có thể bị cancel, tránh memory leak
//ví dụ như người dùng rời màn hình hoặc viewmodel clear thì coroutine sẽ được cancel, suspendCoroutine thì ko
//suspendCancellableCoroutine chuyển callback thành suspend function, khi nhảy vào đến đây thì sẽ bị suspend
//khi call back trả giá trị thì đánh thức lại coroutine dùng resume hoặc resumeWithException (gọi lại lần hai sẽ ném exception)
//có tryResume và tryResumeWithException, hai cái này có thể được gọi lại nhiều hơn 1 lần
//resumeWith sẽ gói cả thành công hay thất bại vào trong 1 Result
class CloudinaryAvatarUploader @Inject constructor() : CloudinaryUploader {
    override suspend fun upload(
        uri: Uri, position: Int?
    ): String = suspendCancellableCoroutine { continuation ->

        continuation.invokeOnCancellation {
            MediaManager.get().cancelAllRequests()
        }

        MediaManager.get().upload(uri)
            .unsigned(StringConstants.UPLOAD_PRESET)
            .option(StringConstants.FOLDER_CLOUDINARY, StringConstants.FOLDER_AVATAR)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

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
                    if (!continuation.isActive) return
                    continuation.resumeWithException(RuntimeException(error?.description))
                }
            }).dispatch()
    }

}