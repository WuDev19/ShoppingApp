package com.example.shoppingapp.data.source.review

import android.content.Context
import android.net.Uri
import com.example.shoppingapp.R
import com.example.shoppingapp.data.remote.api.ReviewAPI
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class BackendReviewImageUploader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reader: ReviewImageReader,
    private val api: ReviewAPI,
    private val moshi: Moshi
) {
    suspend fun upload(uri: Uri): String {
        val payload = reader.read(uri)
        val file = MultipartBody.Part.createFormData("file", "review-image",
            payload.bytes.toRequestBody(payload.contentType.toMediaType()))
        return when (val result = NetworkHelper.callApi(
            apiCall = { api.uploadImage(file) }, mapper = { it.imageUrl }, moshi = moshi
        )) {
            is StateListener.Success -> result.result
            is StateListener.Error -> throw result.e ?: IllegalStateException(context.getString(R.string.review_load_error))
            else -> error("Unexpected upload state")
        }
    }

}
