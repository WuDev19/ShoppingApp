package com.example.shoppingapp.di

import com.example.shoppingapp.BuildConfig
import com.example.shoppingapp.utils.SessionManager
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.TokenManager
import com.example.shoppingapp.data.remote.api.seller.SellerDashboardAPI
import com.example.shoppingapp.data.remote.api.AuthAPI
import com.example.shoppingapp.data.remote.api.CartAPI
import com.example.shoppingapp.data.remote.api.CategoryAPI
import com.example.shoppingapp.data.remote.api.OrderAPI
import com.example.shoppingapp.data.remote.api.PaymentAPI
import com.example.shoppingapp.data.remote.api.ReviewAPI
import com.example.shoppingapp.data.remote.api.product.GetProductAPI
import com.example.shoppingapp.data.remote.api.seller.NormalFunctionSellerAPI
import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.data.remote.api.ghn.AddressAPI
import com.example.shoppingapp.data.remote.api.ghn.ServiceAPI
import com.example.shoppingapp.data.remote.api.product.ModifyProductAPI
import com.example.shoppingapp.data.remote.api.seller.AdvancedFunctionsSellerAPI
import com.example.shoppingapp.data.remote.interceptors.AuthInterceptor
import com.example.shoppingapp.data.remote.interceptors.RefreshTokenInterceptor
import com.example.shoppingapp.di.qualifier.IpAddress
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object APIModule {
    @Provides
    @Singleton
    fun provideSellerDashboardAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit):
        SellerDashboardAPI =
        retrofit.create(SellerDashboardAPI::class.java)


    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideRefreshTokenInterceptor(
        tokenManager: TokenManager,
        sessionManager: SessionManager,
        authAPI: AuthAPI,
        @IpAddress ipAddress: String?
    ): RefreshTokenInterceptor =
        RefreshTokenInterceptor(tokenManager, sessionManager, authAPI, ipAddress)

    @Provides
    @Singleton
    @Named(StringConstants.OKHTTP_INTERCEPTOR)
    fun provideOkhttpClientWithInterceptor(
        authInterceptor: AuthInterceptor,
        refreshTokenInterceptor: RefreshTokenInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(refreshTokenInterceptor)
            .writeTimeout(30, TimeUnit.SECONDS) // quá 30s ko gửi được lên server thì ném ra lỗi
            .readTimeout(30, TimeUnit.SECONDS) // quá 30s ko đọc được dữ liệu từ server thì ném lỗi
            .callTimeout(90, TimeUnit.SECONDS)
            .build()
    //connectionTimeout(10, TimeUnit.SECONDS): là quá 10s chưa kết nối được đến server thì ném lỗi
    //callTimeout(20, TimeUnit.SECONDS): tổng thời gian cho việc phân giải miền dns, connection, read, write, server xử lý là 20s, quá 20s thì ném ra lỗi

    @Provides
    @Singleton
    @Named(StringConstants.OKHTTP_NO_INTERCEPTOR)
    fun provideOkhttpClientWithNoInterceptor(
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @Named(StringConstants.NO_AUTH)
    fun provideRetrofitWithNoAuth(
        moshi: Moshi,
        @Named(StringConstants.OKHTTP_NO_INTERCEPTOR) client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG && BuildConfig.REVIEW_TEST_BASE_URL.isNotBlank())
                BuildConfig.REVIEW_TEST_BASE_URL else StringConstants.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named(StringConstants.NEED_AUTH)
    fun provideRetrofitWithAuth(
        moshi: Moshi,
        @Named(StringConstants.OKHTTP_INTERCEPTOR) client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG && BuildConfig.REVIEW_TEST_BASE_URL.isNotBlank())
                BuildConfig.REVIEW_TEST_BASE_URL else StringConstants.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): UserAPI =
        retrofit.create(UserAPI::class.java)

    @Provides
    @Singleton
    fun provideGetProductAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): GetProductAPI =
        retrofit.create(GetProductAPI::class.java)

    @Provides
    @Singleton
    fun provideModifyProductAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): ModifyProductAPI =
        retrofit.create(ModifyProductAPI::class.java)

    @Provides
    @Singleton
    fun provideNormalFunctionsSellerAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): NormalFunctionSellerAPI =
        retrofit.create(NormalFunctionSellerAPI::class.java)

    @Provides
    @Singleton
    fun provideAdvancedFunctionsSellerAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): AdvancedFunctionsSellerAPI =
        retrofit.create(AdvancedFunctionsSellerAPI::class.java)

    @Provides
    @Singleton
    fun provideCartAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): CartAPI =
        retrofit.create(CartAPI::class.java)

    @Provides
    @Singleton
    fun provideCategoryAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): CategoryAPI =
        retrofit.create(CategoryAPI::class.java)

    @Provides
    @Singleton
    fun provideAuthAPI(@Named(StringConstants.NO_AUTH) retrofit: Retrofit): AuthAPI =
        retrofit.create(AuthAPI::class.java) //tách riêng các retrofit như này thì giúp tránh vòng lặp vô hạn

    @Provides
    @Singleton
    fun provideAddressAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): AddressAPI =
        retrofit.create(AddressAPI::class.java)

    @Provides
    @Singleton
    fun provideServiceAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): ServiceAPI =
        retrofit.create(ServiceAPI::class.java)

    @Provides
    @Singleton
    fun providePaymentAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): PaymentAPI =
        retrofit.create(PaymentAPI::class.java)

    @Provides
    @Singleton
    fun provideOrderAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): OrderAPI =
        retrofit.create(OrderAPI::class.java)

    @Provides
    @Singleton
    fun provideReviewAPI(@Named(StringConstants.NEED_AUTH) retrofit: Retrofit): ReviewAPI =
        retrofit.create(ReviewAPI::class.java)
}