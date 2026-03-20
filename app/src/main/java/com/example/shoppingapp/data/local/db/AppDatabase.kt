package com.example.shoppingapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.example.shoppingapp.data.local.dao.CartDAO
import com.example.shoppingapp.data.local.dao.CartItemDAO
import com.example.shoppingapp.data.local.dao.CategoryAndProductDAO
import com.example.shoppingapp.data.local.dao.DistrictGhnDAO
import com.example.shoppingapp.data.local.dao.ProductDAO
import com.example.shoppingapp.data.local.dao.ProvinceGhnDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyDAO
import com.example.shoppingapp.data.local.dao.RemoteKeyProductDAO
import com.example.shoppingapp.data.local.dao.UserAddressDAO
import com.example.shoppingapp.data.local.dao.WardGhnDAO
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity
import com.example.shoppingapp.data.local.entity.DistrictGhnEntity
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.ProvinceGhnEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity
import com.example.shoppingapp.data.local.entity.RemoteKeyProduct
import com.example.shoppingapp.data.local.entity.UserAddressEntity
import com.example.shoppingapp.data.local.entity.WardGhnEntity

@Database(
    entities = [
        ProductEntity::class,
        RemoteKeyProduct::class,
        RemoteKeyEntity::class,
        CategoryAndProductEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        UserAddressEntity::class,
        ProvinceGhnEntity::class,
        DistrictGhnEntity::class,
        WardGhnEntity::class,
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDAO
    abstract fun remoteKeyProductDao(): RemoteKeyProductDAO
    abstract fun remoteKeyDao(): RemoteKeyDAO
    abstract fun categoryAndProductDao(): CategoryAndProductDAO
    abstract fun cartDao(): CartDAO
    abstract fun cartItemDao(): CartItemDAO
    abstract fun userAddressDao(): UserAddressDAO
    abstract fun provinceGhnDao(): ProvinceGhnDAO
    abstract fun districtGhnDao(): DistrictGhnDAO
    abstract fun wardGhnDao(): WardGhnDAO

}