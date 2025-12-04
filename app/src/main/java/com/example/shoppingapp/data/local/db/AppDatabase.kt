package com.example.shoppingapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppingapp.data.local.dao.CartDAO
import com.example.shoppingapp.data.local.dao.CategoryAndProductDAO
import com.example.shoppingapp.data.local.dao.ProductDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyProductDAO
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyProduct

@Database(
    entities = [
        ProductEntity::class,
        RemoteKeyProduct::class,
        RemoteKeyEntity::class,
        CategoryAndProductEntity::class,
        CartEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDAO
    abstract fun remoteKeyProductDao(): RemoteKeyProductDAO
    abstract fun remoteKeyDao(): RemoteKeyDAO
    abstract fun categoryAndProductDao(): CategoryAndProductDAO
    abstract fun cartDao(): CartDAO

}