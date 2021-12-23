package com.sample.uistatesample.di

import com.sample.uistatesample.api.MockMyApi
import com.sample.uistatesample.api.MyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideMyApi(): MyApi {
        return MockMyApi()
    }
}
