package com.example.shoppingapp.di

import com.example.shoppingapp.data.repository.impl.AuthRepositoryImpl
import com.example.shoppingapp.data.repository.impl.CartRepositoryImpl
import com.example.shoppingapp.data.repository.impl.CategoryRepositoryImpl
import com.example.shoppingapp.data.repository.impl.GhnRepositoryImpl
import com.example.shoppingapp.data.repository.impl.OrderRepositoryImpl
import com.example.shoppingapp.data.repository.impl.ProductRepositoryImpl
import com.example.shoppingapp.data.repository.impl.ReviewRepositoryImpl
import com.example.shoppingapp.data.repository.impl.SellerRepositoryImpl
import com.example.shoppingapp.data.repository.impl.UserRepositoryImpl
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.domain.repository.cart.CartRepository
import com.example.shoppingapp.domain.repository.category.CategoryRepository
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.domain.repository.order.OrderRepository
import com.example.shoppingapp.domain.repository.product.ProductRepository
import com.example.shoppingapp.domain.repository.review.ReviewRepository
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds //ko tạo object thừa, phù hợp với interface
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindSellerRepository(
        impl: SellerRepositoryImpl
    ): SellerRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindGhnRepository(
        impl: GhnRepositoryImpl
    ): GhnRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl
    ): ReviewRepository

}