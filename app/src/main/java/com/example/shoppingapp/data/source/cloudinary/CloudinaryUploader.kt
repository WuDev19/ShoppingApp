package com.example.shoppingapp.data.source.cloudinary

import android.net.Uri

interface CloudinaryUploader{
    fun upload(uri: Uri, onProgress: (Int) -> Unit, onSuccess: (String) -> Unit, onFailure: (String) -> Unit)

}