package com.dv.apna.feature.education.di

import com.dv.apna.feature.education.data.repository.EducationRepositoryImpl
import com.dv.apna.feature.education.domain.repository.EducationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EducationModule {

    @Binds
    @Singleton
    abstract fun bindEducationRepository(
        educationRepositoryImpl: EducationRepositoryImpl
    ): EducationRepository
}
