package com.dv.apna.feature.labour.di

import com.dv.apna.feature.labour.data.repository.LabourRepositoryImpl
import com.dv.apna.feature.labour.domain.repository.LabourRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LabourModule {

    @Binds
    @Singleton
    abstract fun bindLabourRepository(
        labourRepositoryImpl: LabourRepositoryImpl
    ): LabourRepository
}
