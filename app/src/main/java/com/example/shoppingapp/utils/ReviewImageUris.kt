package com.example.shoppingapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ReviewImageUris {
    private fun owns(context: Context, uri: Uri) =
        uri.authority == context.packageName + StringConstants.REVIEW_FILE_AUTHORITY_SUFFIX

    fun createCameraUri(context: Context): Uri? = runCatching {
        val directory = File(context.cacheDir, StringConstants.REVIEW_CAMERA_DIRECTORY)
        check(directory.isDirectory || directory.mkdirs())
        val image = File.createTempFile("capture-", ".jpg", directory)
        FileProvider.getUriForFile(context,
            context.packageName + StringConstants.REVIEW_FILE_AUTHORITY_SUFFIX, image)
    }.getOrNull()

    fun keepReadAccess(context: Context, uri: Uri): Boolean {
        if (owns(context, uri)) return true
        return try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            true
        } catch (_: SecurityException) { false }
    }

    fun discardCameraUri(context: Context, uri: Uri) {
        if (owns(context, uri)) runCatching { context.contentResolver.delete(uri, null, null) }
    }
}
