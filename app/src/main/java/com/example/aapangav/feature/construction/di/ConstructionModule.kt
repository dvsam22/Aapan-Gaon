package com.example.aapangav.feature.construction.di

import com.example.aapangav.feature.construction.data.repository.ConstructionRepositoryImpl
import com.example.aapangav.feature.construction.domain.repository.ConstructionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConstructionModule {
    @Binds
    @Singleton
    abstract fun bindConstructionRepository(impl: ConstructionRepositoryImpl): ConstructionRepository
}