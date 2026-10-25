package com.example.shoppingapp

import com.example.shoppingapp.data.remote.api.seller.SellerDashboardAPI
import com.example.shoppingapp.data.remote.dto.get.SellerDashboardDTO
import com.example.shoppingapp.data.remote.dto.get.SellerMonthlyStatsDTO
import com.example.shoppingapp.data.remote.mapper.toDomain
import com.example.shoppingapp.data.repository.impl.SellerDashboardRepositoryImpl
import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.usecase.seller_usecase.GetSellerDashboardUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response

class DashboardContractTest {
    @Test fun dateRangeAcceptsExactly366InclusiveDays() {
        assertTrue(DashboardFilter("2024-01-01", "2024-12-31", 2024).isValid())
        assertFalse(DashboardFilter("2024-01-01", "2025-01-01", 2024).isValid())
        assertTrue(DashboardFilter("2025-01-01", "2025-01-01", 2025).isValid())
    }
    @Test fun invalidDatesAndReverseRangesAreRejected() {
        assertFalse(DashboardFilter("2025-02-29", "2025-03-01", 2025).isValid())
        assertFalse(DashboardFilter("2025-03-02", "2025-03-01", 2025).isValid())
        assertFalse(DashboardFilter("2025-3-01", "2025-03-01", 2025).isValid())
        assertFalse(DashboardFilter("2025-03-01suffix", "2025-03-01", 2025).isValid())
        assertFalse(DashboardFilter("2025-03-01", "2025-03-01", 1899).isValid())
        assertFalse(DashboardFilter("2025-03-01", "2025-03-01", 9999).isValid())
    }
    @Test fun calendarDateRoundTripDoesNotShiftDay() {
        assertEquals("2024-02-29", DashboardFilter.iso(requireNotNull(DashboardFilter.parse("2024-02-29"))))
        assertEquals("29/02/2024", DashboardFilter.display("2024-02-29"))
        assertTrue(DashboardFilter.current().isValid())
    }
    @Test fun jsonMappingPreservesLongAmountsAndNullableProductMetadata() {
        val json = """{"sellerId":7,"fromDate":"2025-01-01","toDate":"2025-01-02","totalOrders":3000000000,"completedOrders":1,"completedOrderValue":5000000000,"onlinePaidAmount":4000000000,"completedCodOrderValue":1000000000,"unitsSold":3,"undatedCompletedOrders":2,"totalProducts":10,"totalStock":20,"outOfStockVariants":1,"ordersByStatus":[{"status":"Completed","orderCount":1}],"dailySales":[{"date":"2025-01-01","completedOrderValue":5000000000,"onlinePaidAmount":4000000000,"unitsSold":3}],"topProducts":[{"productId":1,"description":null,"thumbnail":null,"unitsSold":3}]}"""
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val result = requireNotNull(moshi.adapter(SellerDashboardDTO::class.java).fromJson(json)).toDomain()
        assertEquals(5000000000L, result.completedOrderValue)
        assertEquals(3000000000L, result.totalOrders)
        assertEquals(4000000000L, result.dailySales.single().onlinePaidAmount)
        assertEquals(2L, result.undatedCompletedOrders)
        assertNull(result.topProducts.single().description)
    }
    @Test fun monthlyResponseKeepsZeroMonths() {
        val json = """{"sellerId":7,"year":2025,"undatedCompletedOrders":1,"months":[{"month":1,"completedOrderValue":0,"onlinePaidAmount":0,"unitsSold":0},{"month":2,"completedOrderValue":3000000000,"onlinePaidAmount":0,"unitsSold":2}]}"""
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val result = requireNotNull(moshi.adapter(SellerMonthlyStatsDTO::class.java).fromJson(json)).toDomain()
        assertEquals(0L, result.months.first().completedOrderValue)
        assertEquals(3000000000L, result.months.last().completedOrderValue)
    }
    @Test fun invalidFilterNeverCallsApi() = runBlocking {
        var calls = 0
        val api = object : SellerDashboardAPI {
            override suspend fun dashboard(fromDate: String, toDate: String): Response<SellerDashboardDTO> { calls++; error("Unexpected API call") }
            override suspend fun monthly(year: Int): Response<SellerMonthlyStatsDTO> = error("Unexpected API call")
        }
        try {
            GetSellerDashboardUseCase(SellerDashboardRepositoryImpl(api))(DashboardFilter("2025-02-29", "2025-03-01", 2025))
            fail("Invalid dates must be rejected")
        } catch (_: IllegalArgumentException) { assertEquals(0, calls) }
    }
    @Test fun repositoryCancellationPropagates() = runBlocking {
        val api = object : SellerDashboardAPI {
            override suspend fun dashboard(fromDate: String, toDate: String): Response<SellerDashboardDTO> = throw CancellationException("screen gone")
            override suspend fun monthly(year: Int): Response<SellerMonthlyStatsDTO> = throw CancellationException("screen gone")
        }
        try {
            SellerDashboardRepositoryImpl(api).dashboard("2025-01-01", "2025-01-02")
            fail("Cancellation must propagate")
        } catch (expected: CancellationException) { assertEquals("screen gone", expected.message) }
    }
}
