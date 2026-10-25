package com.example.shoppingapp

import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.model.seller.DashboardMonth
import com.example.shoppingapp.domain.model.seller.DashboardOrderStatus
import com.example.shoppingapp.domain.model.seller.DashboardDailySales
import com.example.shoppingapp.domain.model.seller.DashboardTopProduct
import com.example.shoppingapp.domain.model.seller.SellerDashboard
import com.example.shoppingapp.domain.model.seller.SellerMonthlyStats
import com.example.shoppingapp.domain.repository.seller.SellerDashboardRepository
import com.example.shoppingapp.domain.usecase.seller_usecase.GetSellerDashboardUseCase
import com.example.shoppingapp.domain.usecase.seller_usecase.GetSellerMonthlyStatsUseCase
import com.example.shoppingapp.ui.state.DashboardError
import com.example.shoppingapp.ui.state.DashboardSection
import com.example.shoppingapp.ui.state.SellerDashboardState
import com.example.shoppingapp.ui.view.adapter.DashBoardSellerAdapter
import com.example.shoppingapp.ui.viewmodel.seller.DashBoardSellerViewModel
import com.example.shoppingapp.utils.DashboardConstants
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class DashboardDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private fun <T> main(block: () -> T): T {
        val result = AtomicReference<T>()
        instrumentation.runOnMainSync { result.set(block()) }
        return result.get()
    }
    private fun await(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 10000
        while (System.currentTimeMillis() < deadline) {
            instrumentation.waitForIdleSync()
            if (main(condition)) return
            Thread.sleep(50)
        }
        fail("Dashboard did not reach expected state")
    }
    private fun summary(from: String = "2025-01-01", to: String = "2025-01-02", seller: Int = 7) = SellerDashboard(
        seller, from, to, 3, 2, 3000000000L, 2000000000L, 1000000000L, 4, 1, 10, 20, 2,
        listOf(DashboardOrderStatus("Completed", 2), DashboardOrderStatus("Processing", 1)),
        listOf(DashboardDailySales(from, 3000000000L, 2000000000L, 4)),
        listOf(DashboardTopProduct(1, "Device test product", null, 4))
    )
    private fun months(year: Int = 2025) = SellerMonthlyStats(7, year, 0,
        (1..12).map { DashboardMonth(it, if (it == 1) 3000000000L else 0, 0, 0) })
    private inner class FakeRepository : SellerDashboardRepository {
        var summaryCalls = 0
        var monthlyCalls = 0
        var getSummary: suspend (String, String) -> SellerDashboard = { from, to -> summary(from, to) }
        var getMonthly: suspend (Int) -> SellerMonthlyStats = { months(it) }
        override suspend fun dashboard(fromDate: String, toDate: String): SellerDashboard { summaryCalls++; return getSummary(fromDate, toDate) }
        override suspend fun monthly(year: Int): SellerMonthlyStats { monthlyCalls++; return getMonthly(year) }
    }
    private fun model(repo: FakeRepository, saved: SavedStateHandle = SavedStateHandle()) =
        DashBoardSellerViewModel(GetSellerDashboardUseCase(repo), GetSellerMonthlyStatsUseCase(repo), saved)

    @Test fun failedRefreshKeepsDataAndRetryRecoversWithoutReloadingOtherSection() {
        val repo = FakeRepository()
        val vm = main { model(repo).also { it.onSession("seller-a") } }
        await { vm.state.value.summary.data != null && vm.state.value.monthly.data != null }
        main { repo.getSummary = { _, _ -> throw IOException("offline") }; vm.retrySummary() }
        await { vm.state.value.summary.error == DashboardError.NETWORK }
        main {
            assertNotNull(vm.state.value.summary.data)
            assertNotNull(vm.state.value.monthly.data)
            assertEquals(1, repo.monthlyCalls)
            repo.getSummary = { from, to -> summary(from, to) }
            vm.retrySummary()
        }
        await { vm.state.value.summary.error == null && !vm.state.value.summary.loading }
        main { vm.onSession(null) }
    }
    @Test fun rapidFilterChangesAndAccountSwitchDiscardOldResponses() {
        val repo = FakeRepository()
        val requests = mutableListOf<CompletableDeferred<SellerDashboard>>()
        repo.getSummary = { _, _ -> CompletableDeferred<SellerDashboard>().also { requests.add(it) }.await() }
        val vm = main { model(repo).also { it.onSession("seller-a") } }
        await { requests.size == 1 }
        main { vm.selectRange("2025-02-01", "2025-02-02") }
        await { requests.size == 2 }
        main { requests[1].complete(summary("2025-02-01", "2025-02-02")) }
        await { vm.state.value.summary.data?.fromDate == "2025-02-01" }
        main { requests[0].complete(summary()); vm.onSession("seller-b") }
        await { requests.size == 3 }
        main {
            assertNull(vm.state.value.summary.data)
            requests[2].complete(summary("2025-02-01", "2025-02-02", seller = 8))
        }
        await { vm.state.value.summary.data?.sellerId == 8 }
        main { vm.onSession(null); assertNull(vm.state.value.summary.data); assertNull(vm.state.value.monthly.data) }
    }
    @Test fun filterRestoresAndInvalidRangeDoesNotFetch() {
        val repo = FakeRepository()
        val saved = SavedStateHandle()
        val vm = main { model(repo, saved).also { it.onSession("seller-a") } }
        await { vm.state.value.summary.data != null }
        main { vm.selectRange("2024-01-01", "2024-12-31"); vm.selectYear(2024) }
        await { !vm.state.value.summary.loading && !vm.state.value.monthly.loading }
        main {
            val calls = repo.summaryCalls
            vm.selectRange("2024-01-01", "2025-01-01")
            assertTrue(vm.state.value.invalidFilter)
            assertEquals(calls, repo.summaryCalls)
            val restored = model(repo, SavedStateHandle(mapOf(
                DashboardConstants.FROM_DATE to saved.get<String>(DashboardConstants.FROM_DATE),
                DashboardConstants.TO_DATE to saved.get<String>(DashboardConstants.TO_DATE),
                DashboardConstants.YEAR to saved.get<Int>(DashboardConstants.YEAR))))
            assertEquals(vm.state.value.filter, restored.state.value.filter)
            assertNull(restored.state.value.summary.data)
            vm.onSession(null)
        }
    }
    @Test fun forbiddenResponseClearsBothSections() {
        val repo = FakeRepository()
        val vm = main { model(repo).also { it.onSession("seller-a") } }
        await { vm.state.value.summary.data != null && vm.state.value.monthly.data != null }
        main {
            repo.getSummary = { _, _ -> throw HttpException(Response.error<String>(403, ResponseBody.create(null, "{}"))) }
            vm.retrySummary()
        }
        await { !vm.state.value.signedIn }
        main { assertNull(vm.state.value.summary.data); assertNull(vm.state.value.monthly.data) }
    }
    private fun host() = ActivityScenario.launch<DashboardTestHostActivity>(
        Intent(instrumentation.targetContext, DashboardTestHostActivity::class.java))
    @Test fun actualFragmentRestoresFilterAndDateDialogWhileSignedOut() {
        host().use { scenario ->
            instrumentation.waitForIdleSync()
            scenario.onActivity { activity ->
                val host = activity.supportFragmentManager.fragments.filterIsInstance<NavHostFragment>().single()
                val fragment = requireNotNull(host.childFragmentManager.primaryNavigationFragment)
                ViewModelProvider(fragment)[DashBoardSellerViewModel::class.java].selectRange("2024-02-01", "2024-02-29")
            }
            instrumentation.waitForIdleSync()
            scenario.onActivity { it.findViewById<android.view.View>(R.id.btn_range).performClick() }
            instrumentation.waitForIdleSync()
            scenario.recreate()
            instrumentation.waitForIdleSync()
            scenario.onActivity { activity ->
                assertTrue(activity.findViewById<TextView>(R.id.btn_range).text.contains("29/02/2024"))
                val host = activity.supportFragmentManager.fragments.filterIsInstance<NavHostFragment>().single()
                val fragment = requireNotNull(host.childFragmentManager.primaryNavigationFragment)
                assertNotNull(fragment.childFragmentManager.findFragmentByTag(DashboardConstants.DATE_RANGE_TAG))
                assertFalse(ViewModelProvider(fragment)[DashBoardSellerViewModel::class.java].state.value.signedIn)
            }
        }
    }
    @Test fun adapterRendersLongAmountsAndIndependentErrorEmptyStates() {
        host().use { scenario ->
            instrumentation.waitForIdleSync()
            scenario.onActivity { activity ->
                val recycler = activity.findViewById<RecyclerView>(R.id.recycler_view_dash_board)
                recycler.adapter = DashBoardSellerAdapter({}, {}).apply {
                    submitState(SellerDashboardState(DashboardFilter("2025-01-01", "2025-01-02", 2025),
                        DashboardSection(summary()), DashboardSection(months()), signedIn = true))
                }
            }
            instrumentation.waitForIdleSync()
            Thread.sleep(300)
            val screenshot = instrumentation.uiAutomation.takeScreenshot()
            java.io.File(instrumentation.targetContext.cacheDir, "dashboard-render.png").outputStream().use {
                screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
            }
            screenshot.recycle()
            scenario.onActivity { activity ->
                assertTrue(activity.findViewById<TextView>(R.id.summary_text).text.contains("3.000.000.000"))
                assertTrue(activity.findViewById<TextView>(R.id.monthly_text).text.contains("3.000.000.000"))
                assertTrue(activity.findViewById<TextView>(R.id.status_text).text.contains("2"))
                val recycler = activity.findViewById<RecyclerView>(R.id.recycler_view_dash_board)
                (recycler.adapter as DashBoardSellerAdapter).submitState(SellerDashboardState(DashboardFilter.current(),
                    DashboardSection(summary(), error = DashboardError.NETWORK),
                    DashboardSection(months().copy(months = (1..12).map { DashboardMonth(it, 0, 0, 0) })), signedIn = true))
            }
            instrumentation.waitForIdleSync()
            scenario.onActivity { activity ->
                assertEquals(android.view.View.VISIBLE, activity.findViewById<TextView>(R.id.summary_error).visibility)
                assertEquals(android.view.View.VISIBLE, activity.findViewById<TextView>(R.id.monthly_empty).visibility)
                assertEquals(android.view.View.VISIBLE, activity.findViewById<android.view.View>(R.id.summary_content).visibility)
            }
        }
    }
}
