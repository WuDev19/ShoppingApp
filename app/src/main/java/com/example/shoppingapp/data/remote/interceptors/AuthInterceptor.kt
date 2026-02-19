package com.example.shoppingapp.data.remote.interceptors

import com.example.shoppingapp.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (private val tokenManager: TokenManager) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenManager.getAccessToken()
        token?.let { //nếu token khác null thì tạo request mới từ request cũ để gắn token
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(request)
    }
}