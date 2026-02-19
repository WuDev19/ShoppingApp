package com.example.shoppingapp.data.remote.interceptors

import com.example.shoppingapp.utils.SessionManager
import com.example.shoppingapp.utils.TokenManager
import com.example.shoppingapp.data.remote.api.AuthAPI
import com.example.shoppingapp.data.remote.dto.request.RefreshRequest
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class RefreshTokenInterceptor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val authAPI: AuthAPI,
    private val ipAddress: String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request() //lấy request hiện tại
        val response = chain.proceed(request)
        if (response.code == 401) {
            val oldToken = tokenManager.getAccessToken()
            synchronized(this) {
                val currentToken = tokenManager.getAccessToken()
                if (oldToken != null && oldToken != currentToken) { //nếu thay đổi token rồi thì dùng token mới để gửi request
                    response.close() //đóng response cũ
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(newRequest)
                }

                response.close() // đóng cái response cũ để gửi request refresh token

                val refreshToken = tokenManager.getRefreshToken()
                val newResponse =
                    authAPI.refresh(RefreshRequest(currentToken!!, refreshToken!!, ipAddress))
                        .execute()
                if (!newResponse.isSuccessful) {
                    tokenManager.deleteAll()
                    sessionManager.postEvent()
                    throw IOException("Login session has expired, please log in again")
                }

                val newBothToken = newResponse.body()!!
                val newAccessToken = newBothToken.token
                tokenManager.writeAll(newBothToken)

                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return response
    }
}