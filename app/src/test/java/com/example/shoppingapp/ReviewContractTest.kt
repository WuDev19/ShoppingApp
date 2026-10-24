package com.example.shoppingapp

import com.example.shoppingapp.data.remote.dto.get.ProductReviewResponse
import com.example.shoppingapp.data.remote.dto.get.ProductReviewListItemResponse
import com.example.shoppingapp.data.remote.dto.get.ProductRatingStatsResponse
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.data.remote.mapper.ReviewMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class ReviewContractTest {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Test fun ratingOnlyReviewAcceptsNullCommentAndNoImages() {
        val json = """{"reviewId":1,"rating":5,"comment":null,"createdAt":1,"updatedAt":0,"isDelete":false,"productId":7,"images":[]}"""
        val response = requireNotNull(moshi.adapter(ProductReviewResponse::class.java).fromJson(json))
        val review = ReviewMapper.mapToProductReview(response)
        assertEquals("", review.comment)
        assertEquals(5, review.rating)
        assertTrue(review.images.isEmpty())
    }

    @Test fun reviewListAcceptsNullableAuthorFieldsAndImageIds() {
        val json = """{"reviewId":2,"rating":4,"comment":null,"createdAt":1,"updatedAt":2,"productId":7,"userId":null,"userName":null,"userAvatar":null,"images":[{"imageId":10,"imageUrl":"https://example.com/a.jpg"}]}"""
        val response = requireNotNull(moshi.adapter(ProductReviewListItemResponse::class.java).fromJson(json))
        assertNull(response.userName)
        assertEquals(10, response.images.single().imageId)
    }

    @Test fun ratingStatsPreserveCountsLargerThanInt() {
        val json = """{"averageRating":5.0,"totalReviews":3000000000,"fiveStar":3000000000,"fourStar":0,"threeStar":0,"twoStar":0,"oneStar":0}"""
        val response = requireNotNull(moshi.adapter(ProductRatingStatsResponse::class.java).fromJson(json))
        assertEquals(3000000000L, response.totalReviews)
        assertEquals(3000000000L, response.fiveStar)
    }

    @Test fun apiCancellationIsNotConvertedToAnErrorState() = runBlocking {
        try {
            NetworkHelper.callApi<String, String>(
                apiCall = { throw CancellationException("screen closed") },
                mapper = { it }, moshi = moshi
            )
            fail("Cancellation must propagate")
        } catch (expected: CancellationException) {
            assertEquals("screen closed", expected.message)
        }
    }
}
