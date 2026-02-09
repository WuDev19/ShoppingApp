package com.example.shoppingapp.domain.usecase.productusecase.post

import android.net.Uri
import com.example.shoppingapp.domain.repository.product.ProductRepository
import javax.inject.Inject

class UploadImageProductUseCase @Inject constructor(private val productRepository: ProductRepository) {

    suspend operator fun invoke(uri: Uri, position: Int): String =
        productRepository.uploadImageDetail(uri, position)

}