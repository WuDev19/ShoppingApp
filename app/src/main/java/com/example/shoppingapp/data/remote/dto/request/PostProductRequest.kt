package com.example.shoppingapp.data.remote.dto.request

data class PostProductRequest(
    val generalPrice: Int,
    val description: String,
    var thumbnail: String?,
    val discount: Int?,
    val introduction: String?,
    var productDetails: List<PostProductDetailRequest>,
    var imageDescriptionProducts: List<ImageDescriptionProduct>?,
    val choseCategoryRequests: List<ChoseCategoryRequest>
)
