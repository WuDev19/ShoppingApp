package com.example.shoppingapp.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IpAddress

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceId
