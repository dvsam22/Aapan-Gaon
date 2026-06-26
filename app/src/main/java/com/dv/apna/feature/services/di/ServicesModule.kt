package com.dv.apna.feature.services.di

import com.dv.apna.feature.services.data.repository.ServicesRepositoryImpl
import com.dv.apna.feature.services.domain.repository.ServicesRepository
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