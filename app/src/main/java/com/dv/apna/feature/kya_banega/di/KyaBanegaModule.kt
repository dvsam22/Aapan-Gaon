package com.dv.apna.feature.kya_banega.di

import com.dv.apna.feature.kya_banega.data.repository.KyaBanegaRepositoryImpl
import com.dv.apna.feature.kya_banega.domain.repository.KyaBanegaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KyaBanegaModule {

    @Binds
    @Singleton
    abstract fun bindKyaBanegaRepository(
        impl: KyaBanegaRepositoryImpl
    ): KyaBanegaRepository
}
