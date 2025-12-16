package com.example.shoppingapp.di.qualifier

import javax.inject.Qualifier

//AnnotationRetention.SOURCE: biến mất sau khi compile, ko phù hợp cho hilt
//AnnotationRetention.RUNTIME: sau khi biên dịch vẫn còn, và hiển thị với reflection (Java dùng khi làm việc với Hilt)
@Qualifier
@Retention(AnnotationRetention.BINARY) //hilt đọc được lúc compile, sau khi compile vẫn còn nhưng ko hiển thị với reflection
annotation class ImageProductUploader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoProductUploader

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AvatarUploader