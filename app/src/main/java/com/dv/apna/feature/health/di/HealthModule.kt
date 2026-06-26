package com.dv.apna.feature.health.di

import com.dv.apna.feature.health.data.repository.HealthRepositoryImpl
import com.dv.apna.feature.health.domain.repository.HealthRepository
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