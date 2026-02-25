package com.example.shoppingapp.di

import android.content.Context
import androidx.room.Room
import com.example.shoppingapp.data.local.dao.CartDAO
import com.example.shoppingapp.data.local.dao.CartItemDAO
import com.example.shoppingapp.data.local.dao.CategoryAndProductDAO
import com.example.shoppingapp.data.local.dao.ProductDAO
import com.example.shoppingapp.data.local.dao.ProvinceGhnDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyProductDAO
import com.example.shoppingapp.data.local.dao.UserAddressDAO
import com.example.shoppingapp.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            AppDatabase::class.java,
            "shopping_app"
        ).build()
    }

    @Singleton
    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDAO = appDatabase.productDao()

    @Singleton
    @Provides
    fun provideRemoteKeyProductDao(appDatabase: AppDatabase): RemoteKeyProductDAO =
        appDatabase.remoteKeyProductDao()

    @Singleton
    @Provides
    fun provideRemoteKeyDao(appDatabase: AppDatabase): RemoteKeyDAO = appDatabase.remoteKeyDao()

    @Singleton
    @Provides
    fun provideCategoryAndProductDao(appDatabase: AppDatabase): CategoryAndProductDAO =
        appDatabase.categoryAndProductDao()

    @Singleton
    @Provides
    fun provideCartDao(appDatabase: AppDatabase): CartDAO = appDatabase.cartDao()

    @Singleton
    @Provides
    fun provideCartItemDao(appDatabase: AppDatabase): CartItemDAO = appDatabase.cartItemDao()

    @Singleton
    @Provides
    fun provideUserAddressDao(appDatabase: AppDatabase): UserAddressDAO =
        appDatabase.userAddressDao()

    @Singleton
    @Provides
    fun provideProvinceGhnDao(appDatabase: AppDatabase): ProvinceGhnDAO =
        appDatabase.provinceGhnDao()

}

