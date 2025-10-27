package com.example.shoppingapp.ui.state

import java.lang.Exception

sealed class StateListener <out T>{
    data object Loading : StateListener<Nothing>()
    data object Idle : StateListener<Nothing>()
    data class Success<out T>(val result : T) : StateListener<T>()
    data class Error(var e: Exception?) : StateListener<Nothing>()
}