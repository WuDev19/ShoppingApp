package com.example.shoppingapp.data.source.cloudinary.impl

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shoppingapp.utils.UploadingManager
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.collections.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//khai báo cách khởi tạo class như này, bới vì đây là class mình tự tạo
//dùng @Module + @Provides cũng được nhưng nên dùng cho các thư viện ngoài ko sửa được source
class CloudinaryImageProductUploader @Inject constructor(
    private val uploadingManager: UploadingManager
) : CloudinaryUploader {
    override suspend fun upload(
        uri: Uri, position: Int?
    ): String = suspendCancellableCoroutine { continuation ->

        continuation.invokeOnCancellation {
            MediaManager.get().cancelAllRequests()
        }

        MediaManager.get().upload(uri)
            .unsigned(StringConstants.UPLOAD_PRESET)
            .option(StringConstants.FOLDER_CLOUDINARY, StringConstants.FOLDER_PRODUCT_IMAGE)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {
                    if(position != -1){
                        uploadingManager.pushPercent(
                            position!!,
                            bytes.toFloat() / totalBytes * 100,
                            requestId,
                            uri
                        )
                    }
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