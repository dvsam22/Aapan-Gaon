package com.dv.apna.feature.transport.di

import com.dv.apna.feature.transport.data.repository.TransportRepositoryImpl
import com.dv.apna.feature.transport.domain.repository.TransportRepository
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