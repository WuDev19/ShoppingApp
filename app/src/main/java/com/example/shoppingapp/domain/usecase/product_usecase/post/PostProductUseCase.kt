package com.example.shoppingapp.domain.usecase.product_usecase.post

import com.example.shoppingapp.data.remote.dto.request.PostProductRequest
import com.example.shoppingapp.domain.repository.product.ProductRepository
import javax.inject.Inject

// TODO: refractor model
class PostProductUseCase @Inject constructor(private val productRepository: ProductRepository) {

    suspend operator fun invoke(sellerId: Int, postProductRequest: PostProductRequest): Int =
        productRepository.postProduct(sellerId, postProductRequest)

}