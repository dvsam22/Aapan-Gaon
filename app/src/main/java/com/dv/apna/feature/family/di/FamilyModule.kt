package com.dv.apna.feature.family.di

import com.dv.apna.feature.family.data.repository.FamilyFunctionRepositoryImpl
import com.dv.apna.feature.family.domain.repository.FamilyFunctionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FamilyModule {

    @Binds
    @Singleton
    abstract fun bindFamilyFunctionRepository(
        familyFunctionRepositoryImpl: FamilyFunctionRepositoryImpl
    ): FamilyFunctionRepository
}
