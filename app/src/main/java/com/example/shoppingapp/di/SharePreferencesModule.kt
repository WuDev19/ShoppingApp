package com.example.shoppingapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
- SingletonComponent: Sống theo toàn app (khi app bị kill thì mới die)
- ActivityRetainedComponent: Sống theo Activity nhưng KHÔNG chết khi xoay màn hình
- ViewModelComponent: Sống theo vòng đời viewmodel
- ActivityComponent: Sống theo vòng đời activity, khi xoay màn hình thì cũng bị tạo lại
- FragmentComponent: Sống theo vòng đời fragment
- ViewComponent: Sống theo vòng đời của view (trong activity, custom view, ...)
- ViewWithFragmentComponent: Sống theo vòng đời của view trong fragment
- ServiceComponent: Dành cho service
*/

@Module
@InstallIn(SingletonComponent::class)
object SharePreferencesModule {

    @Provides
    @Singleton
    fun provideSharePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("shopping_app", Context.MODE_PRIVATE)
    }

}