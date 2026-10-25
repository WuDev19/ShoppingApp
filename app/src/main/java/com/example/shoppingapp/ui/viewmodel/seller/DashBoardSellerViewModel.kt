package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.seller.DashboardFilter
import com.example.shoppingapp.domain.usecase.seller_usecase.GetSellerDashboardUseCase
import com.example.shoppingapp.domain.usecase.seller_usecase.GetSellerMonthlyStatsUseCase
import com.example.shoppingapp.ui.state.DashboardError
import com.example.shoppingapp.ui.state.DashboardSection
import com.example.shoppingapp.ui.state.SellerDashboardState
import com.example.shoppingapp.utils.DashboardConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DashBoardSellerViewModel @Inject constructor(
    private val dashboard: GetSellerDashboardUseCase,
    private val monthly: GetSellerMonthlyStatsUseCase,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val defaultFilter = DashboardFilter.current()
    private val restoredFilter = DashboardFilter(
        savedState[DashboardConstants.FROM_DATE] ?: defaultFilter.fromDate,
        savedState[DashboardConstants.TO_DATE] ?: defaultFilter.toDate,
        savedState[DashboardConstants.YEAR] ?: defaultFilter.year
    ).takeIf { it.isValid() } ?: defaultFilter
    private val _state = MutableStateFlow(SellerDashboardState(restoredFilter))
    val state = _state.asStateFlow()
    private var owner: String? = null
    private var refreshOnReturn = false

    fun markRefreshOnReturn() { refreshOnReturn = true }
    fun refreshIfNeeded() {
        if (refreshOnReturn) {
            refreshOnReturn = false
            refresh()
        }
    }
    private fun denyAccess() {
        summaryJob?.cancel()
        monthlyJob?.cancel()
        owner = null
        _state.update { SellerDashboardState(it.filter) }
    }
    private var summaryJob: Job? = null
    private var monthlyJob: Job? = null

    fun onSession(userId: String?) {
        if (owner == userId && _state.value.signedIn == !userId.isNullOrBlank()) return
        summaryJob?.cancel()
        monthlyJob?.cancel()
        owner = userId?.takeIf { it.isNotBlank() }
        _state.value = SellerDashboardState(_state.value.filter, signedIn = owner != null)
        if (owner != null) refresh()
    }

    fun selectRange(fromDate: String, toDate: String) = applyFilter(_state.value.filter.copy(fromDate = fromDate, toDate = toDate))
    fun selectYear(year: Int) = applyFilter(_state.value.filter.copy(year = year))

    private fun applyFilter(filter: DashboardFilter) {
        if (!filter.isValid()) {
            _state.update { it.copy(invalidFilter = true) }
            return
        }
        val previous = _state.value.filter
        savedState[DashboardConstants.FROM_DATE] = filter.fromDate
        savedState[DashboardConstants.TO_DATE] = filter.toDate
        savedState[DashboardConstants.YEAR] = filter.year
        _state.update { it.copy(filter = filter, invalidFilter = false) }
        if (owner == null) return
        if (previous.fromDate != filter.fromDate || previous.toDate != filter.toDate) loadSummary(false)
        if (previous.year != filter.year) loadMonthly(false)
    }

    fun refresh() {
        if (owner == null) return
        loadSummary(true)
        loadMonthly(true)
    }
    fun retrySummary() { if (owner != null) loadSummary(true) }
    fun retryMonthly() { if (owner != null) loadMonthly(true) }

    private fun loadSummary(keepData: Boolean) {
        if (owner == null) return
        summaryJob?.cancel()
        val filter = _state.value.filter
        val requestedOwner = owner
        _state.update { it.copy(summary = DashboardSection(if (keepData) it.summary.data else null, loading = true)) }
        summaryJob = viewModelScope.launch {
            try {
                val result = dashboard(filter)
                currentCoroutineContext().ensureActive()
                if (owner == requestedOwner && _state.value.filter.fromDate == filter.fromDate && _state.value.filter.toDate == filter.toDate) {
                    _state.update { it.copy(summary = DashboardSection(data = result)) }
                }
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) {
                currentCoroutineContext().ensureActive()
                if (classify(error) == DashboardError.ACCESS) {
                    denyAccess()
                    return@launch
                }
                _state.update { it.copy(summary = it.summary.copy(loading = false, error = classify(error))) }
            }
        }
    }
    private fun loadMonthly(keepData: Boolean) {
        if (owner == null) return
        monthlyJob?.cancel()
        val year = _state.value.filter.year
        val requestedOwner = owner
        _state.update { it.copy(monthly = DashboardSection(if (keepData) it.monthly.data else null, loading = true)) }
        monthlyJob = viewModelScope.launch {
            try {
                val result = monthly(year)
                currentCoroutineContext().ensureActive()
                if (owner == requestedOwner && _state.value.filter.year == year) {
                    _state.update { it.copy(monthly = DashboardSection(data = result)) }
                }
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) {
                currentCoroutineContext().ensureActive()
                if (classify(error) == DashboardError.ACCESS) {
                    denyAccess()
                    return@launch
                }
                _state.update { it.copy(monthly = it.monthly.copy(loading = false, error = classify(error))) }
            }
        }
    }
    private fun classify(error: Exception): DashboardError = when {
        error is HttpException && error.code() in listOf(DashboardConstants.HTTP_UNAUTHORIZED, DashboardConstants.HTTP_FORBIDDEN) -> DashboardError.ACCESS
        error is IOException -> DashboardError.NETWORK
        else -> DashboardError.SERVER
    }
}
