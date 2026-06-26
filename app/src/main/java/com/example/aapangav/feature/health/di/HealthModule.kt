package com.example.aapangav.feature.health.di

import com.example.aapangav.feature.health.data.repository.HealthRepositoryImpl
import com.example.aapangav.feature.health.domain.repository.HealthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HealthModule {
    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository
}