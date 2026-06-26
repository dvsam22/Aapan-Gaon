package com.dv.apna.feature.splash.di

import com.dv.apna.feature.splash.data.repository.SplashRepositoryImpl
import com.dv.apna.feature.splash.domain.repository.SplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashModule {
    @Binds
    @Singleton
    abstract fun bindSplashRepository(impl: SplashRepositoryImpl): SplashRepository
}