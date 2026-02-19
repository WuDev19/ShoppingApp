package com.example.shoppingapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager {

    private val _needLogoutState = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val needLogoutState = _needLogoutState.asSharedFlow()

    fun postEvent(){
        _needLogoutState.tryEmit(true)
    }

}