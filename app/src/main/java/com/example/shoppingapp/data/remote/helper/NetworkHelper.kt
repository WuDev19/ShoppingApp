package com.example.shoppingapp.data.remote.helper

import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import com.squareup.moshi.Moshi
import retrofit2.Response

object NetworkHelper {
    suspend fun <T, R> callApi(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R,
        moshi: Moshi
    ): StateListener<R> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(mapper(it))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }
    }
}