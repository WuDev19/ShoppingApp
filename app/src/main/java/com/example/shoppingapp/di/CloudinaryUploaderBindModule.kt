package com.example.shoppingapp.di

import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryImageReviewUploader
import com.example.shoppingapp.di.qualifier.ImageReviewUploader
import com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryAvatarUploader
import com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryImageProductUploader
import com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryVideoProductUploader
import com.example.shoppingapp.di.qualifier.AvatarUploader
import com.example.shoppingapp.di.qualifier.ImageProductUploader
import com.example.shoppingapp.di.qualifier.VideoProductUploader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudinaryUploaderBindModule {

    @Binds
    @Singleton
    @AvatarUploader
    abstract fun provideCloudinaryAvatarUploader(impl: CloudinaryAvatarUploader): CloudinaryUploader

    @Binds
    @Singleton
    @ImageProductUploader
    abstract fun provideCloudinaryImageProductUploader(impl: CloudinaryImageProductUploader): CloudinaryUploader

    @Binds
    @Singleton
    @VideoProductUploader
    abstract fun provideCloudinaryVideoProductUploader(impl: CloudinaryVideoProductUploader): CloudinaryUploader


    @Binds
    @Singleton
    @ImageReviewUploader
    abstract fun provideCloudinaryImageReviewUploader(impl: CloudinaryImageReviewUploader): CloudinaryUploader
}