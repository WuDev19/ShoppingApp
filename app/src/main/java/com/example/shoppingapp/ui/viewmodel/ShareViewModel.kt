package com.example.shoppingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor() : ViewModel(){

    private val _state = MutableStateFlow<String?>(null)
    val state: StateFlow<String?> = _state

    fun shareName(name: String){
        _state.value = name
    }

}