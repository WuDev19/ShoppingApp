package com.example.shoppingapp.utils

import android.content.Context
import android.net.Uri
import java.security.MessageDigest

object FileHashUtils {

    fun sha1(context: Context, uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-1")
        context.contentResolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

}