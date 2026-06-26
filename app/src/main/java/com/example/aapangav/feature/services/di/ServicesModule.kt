package com.example.aapangav.feature.services.di

import com.example.aapangav.feature.services.data.repository.ServicesRepositoryImpl
import com.example.aapangav.feature.services.domain.repository.ServicesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {
    @Binds
    @Singleton
    abstract fun bindServicesRepository(impl: ServicesRepositoryImpl): ServicesRepository
}