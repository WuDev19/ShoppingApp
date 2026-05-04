package com.example.shoppingapp.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

object UriHelper {

    fun createImageUri(context: Context, mimeType: String = "image/jpeg"): Uri? {
        val extension = when (mimeType) {
            "image/png" -> ".png"
            "image/webp" -> ".webp"
            else -> ".jpg"
        }
        // tạo contentvalues để ghi vào csdl của máy
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}$extension")
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/ShoppingApp"
            )
        }
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // luu vao bo nho dung chung
            contentValues
        )
    }
}