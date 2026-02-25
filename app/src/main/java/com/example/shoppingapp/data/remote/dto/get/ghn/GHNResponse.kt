package com.example.shoppingapp.data.remote.dto.get.ghn

data class GHNResponse<T> (val code: Int, val message: String, val data: T)