package com.noemi.elogin.di

import com.noemi.elogin.network.ApiService
import com.noemi.elogin.network.ApiServiceImpl
import com.noemi.elogin.repository.LogInRepository
import com.noemi.elogin.repository.LogInRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesApiService(): ApiService = ApiServiceImpl()

    @Provides
    @Singleton
    fun providesLogInRepository(apiService: ApiService): LogInRepository = LogInRepositoryImpl(apiService)
}