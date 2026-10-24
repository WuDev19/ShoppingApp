package com.example.shoppingapp.data.source.review

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.shoppingapp.R
import com.example.shoppingapp.utils.StringConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class ReviewImagePayload(val bytes: ByteArray, val contentType: String)

class ReviewImageReader @Inject constructor(@ApplicationContext private val context: Context) {
    fun read(uri: Uri): ReviewImagePayload {
        fun invalid(): Nothing = throw IllegalArgumentException(context.getString(R.string.review_image_invalid))
        val output = ByteArrayOutputStream()
        val input = context.contentResolver.openInputStream(uri) ?: invalid()
        input.use { source ->
            val buffer = ByteArray(StringConstants.REVIEW_IMAGE_BUFFER_BYTES)
            var count = source.read(buffer)
            while (count != -1) {
                if (output.size() + count > StringConstants.REVIEW_MAX_IMAGE_BYTES) invalid()
                output.write(buffer, 0, count)
                count = source.read(buffer)
            }
        }
        val bytes = output.toByteArray()
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        val type = options.outMimeType
        if (options.outWidth <= 0 || options.outHeight <= 0 ||
            options.outWidth > StringConstants.REVIEW_MAX_IMAGE_DIMENSION ||
            options.outHeight > StringConstants.REVIEW_MAX_IMAGE_DIMENSION ||
            (type != "image/jpeg" && type != "image/png")) invalid()
        return ReviewImagePayload(bytes, requireNotNull(type))
    }
}
