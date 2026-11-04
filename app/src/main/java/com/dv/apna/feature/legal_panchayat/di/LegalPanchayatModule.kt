package com.dv.apna.feature.legal_panchayat.di

import com.dv.apna.feature.legal_panchayat.data.repository.LegalPanchayatRepositoryImpl
import com.dv.apna.feature.legal_panchayat.domain.repository.LegalPanchayatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LegalPanchayatModule {

    @Binds
    @Singleton
    abstract fun bindLegalPanchayatRepository(
        legalPanchayatRepositoryImpl: LegalPanchayatRepositoryImpl
    ): LegalPanchayatRepository
}
