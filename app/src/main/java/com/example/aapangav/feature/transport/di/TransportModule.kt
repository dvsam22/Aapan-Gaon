package com.example.aapangav.feature.transport.di

import com.example.aapangav.feature.transport.data.repository.TransportRepositoryImpl
import com.example.aapangav.feature.transport.domain.repository.TransportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TransportModule {
    @Binds
    @Singleton
    abstract fun bindTransportRepository(impl: TransportRepositoryImpl): TransportRepository
}