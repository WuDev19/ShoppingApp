package com.example.shoppingapp.di

import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.api.CartAPI
import com.example.shoppingapp.data.remote.api.CategoryAPI
import com.example.shoppingapp.data.remote.api.ProductAPI
import com.example.shoppingapp.data.remote.api.SellerAPI
import com.example.shoppingapp.data.remote.api.UserAPI
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object APIModule{

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .baseUrl(StringConstants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserAPI(retrofit: Retrofit) : UserAPI = retrofit.create(UserAPI::class.java)

    @Provides
    @Singleton
    fun provideProductAPI(retrofit: Retrofit) : ProductAPI = retrofit.create(ProductAPI::class.java)

    @Provides
    @Singleton
    fun provideSellerAPI(retrofit: Retrofit) : SellerAPI = retrofit.create(SellerAPI::class.java)

    @Provides
    @Singleton
    fun provideCartAPI(retrofit: Retrofit) : CartAPI = retrofit.create(CartAPI::class.java)

    @Provides
    @Singleton
    fun provideCategoryAPI(retrofit: Retrofit) : CategoryAPI = retrofit.create(CategoryAPI::class.java)

}