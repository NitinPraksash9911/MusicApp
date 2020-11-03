package com.example.musicapp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {


    @Provides
    fun provideApplication(): Application {

        return application
    }
}