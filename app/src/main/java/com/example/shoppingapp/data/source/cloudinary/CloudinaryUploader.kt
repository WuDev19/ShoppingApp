package com.example.shoppingapp.data.source.cloudinary

import android.net.Uri

interface CloudinaryUploader{
    suspend fun upload(uri: Uri): String

}