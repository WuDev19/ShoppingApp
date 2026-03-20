package com.example.shoppingapp.ui.viewmodel.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.UpdateStatusOrderUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.ConfirmChangeEmailUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.LogoutUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val confirmChangeEmailUseCase: ConfirmChangeEmailUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateStatusOrderUseCase: UpdateStatusOrderUseCase,
    val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private val _updateEmailState: MutableStateFlow<StateListener<Unit>> =
        MutableStateFlow(StateListener.Idle)
    val updateEmailState = _updateEmailState.asStateFlow()
    private val _logoutState: MutableStateFlow<StateListener<Boolean>> =
        MutableStateFlow(StateListener.Idle)
    val logoutState = _logoutState.asStateFlow()
    private val _updateStatusOrderState = Channel<StateListener<String>>()
    val updateStatusOrderState = _updateStatusOrderState.receiveAsFlow()
    fun confirmUpdateEmail() {
        viewModelScope.launch {
            try {
                auth.currentUser?.reload()?.await()
                confirmChangeEmailUseCase(
                    auth.currentUser?.uid,
                    auth.currentUser?.email
                ).collectLatest {
                    _updateEmailState.value = it
                }
            } catch (e: Exception) {
                _updateEmailState.value = StateListener.Error(e)
            }

        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collectLatest {
                _logoutState.value = it
            }
        }
    }

    fun writeStateEmailVerify() {
        sharedPreferences.edit().apply {
            putBoolean(StringConstants.SHARE_VERIFY_EMAIL, true)
            apply()
        }
    }

    fun getStateEmailVerify(): Boolean =
        sharedPreferences.getBoolean(StringConstants.SHARE_VERIFY_EMAIL, true)

    fun updateStatusOrder(orderId: String) {
        viewModelScope.launch {
            _updateStatusOrderState.send(StateListener.Loading)
            val result = updateStatusOrderUseCase(orderId)
            if (result is StateListener.Error) {
                _updateStatusOrderState.send(StateListener.Error(result.e))
            } else if (result is StateListener.Success) {
                val data = result.result
                _updateStatusOrderState.send(StateListener.Success(data.orderId))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateStatusOrder(orderIds: List<String>) {
        viewModelScope.launch {
            _updateStatusOrderState.send(StateListener.Loading)
            val listResult = orderIds.asFlow()
                .flatMapMerge(concurrency = 5) { orderId ->
                    flow {
                        emit(updateStatusOrderUseCase(orderId))
                    }
                }.filterIsInstance<StateListener.Success<Order>>()
                .toList()
            if (listResult.size == orderIds.size) {
                val orderId = listResult.joinToString("_") { it.result.orderId }
                _updateStatusOrderState.send(StateListener.Success(orderId))
            } else {
                _updateStatusOrderState.send(StateListener.Error(Exception("Order status update failed")))
            }
        }
    }

}