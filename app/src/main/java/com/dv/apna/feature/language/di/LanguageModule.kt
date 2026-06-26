package com.dv.apna.feature.language.di

import com.dv.apna.feature.language.data.repository.LanguageRepositoryImpl
import com.dv.apna.feature.language.domain.repository.LanguageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LanguageModule {
    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository
}