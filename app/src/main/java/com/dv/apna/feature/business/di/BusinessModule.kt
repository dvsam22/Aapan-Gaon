package com.dv.apna.feature.business.di

import com.dv.apna.feature.business.data.repository.BusinessRepositoryImpl
import com.dv.apna.feature.business.domain.repository.BusinessRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BusinessModule {

    @Binds
    @Singleton
    abstract fun bindBusinessRepository(
        impl: BusinessRepositoryImpl
    ): BusinessRepository
}
