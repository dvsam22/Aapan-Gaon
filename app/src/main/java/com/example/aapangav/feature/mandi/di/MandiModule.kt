package com.example.aapangav.feature.mandi.di

import com.example.aapangav.feature.mandi.data.repository.MandiRepositoryImpl
import com.example.aapangav.feature.mandi.domain.repository.MandiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MandiModule {
    @Binds
    @Singleton
    abstract fun bindMandiRepository(impl: MandiRepositoryImpl): MandiRepository
}