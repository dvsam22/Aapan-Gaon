package com.dv.apna.feature.news.di

import com.dv.apna.feature.news.data.repository.NewsRepositoryImpl
import com.dv.apna.feature.news.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}