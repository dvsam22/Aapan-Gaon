package com.dv.apna.feature.dignitaries.di

import com.dv.apna.feature.dignitaries.data.repository.DignitariesRepositoryImpl
import com.dv.apna.feature.dignitaries.domain.repository.DignitariesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DignitariesModule {

    @Binds
    @Singleton
    abstract fun bindDignitariesRepository(
        impl: DignitariesRepositoryImpl
    ): DignitariesRepository
}
