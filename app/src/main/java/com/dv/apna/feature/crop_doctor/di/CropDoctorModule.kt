package com.dv.apna.feature.crop_doctor.di

import com.dv.apna.feature.crop_doctor.data.repository.CropDoctorRepositoryImpl
import com.dv.apna.feature.crop_doctor.domain.repository.CropDoctorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CropDoctorModule {
    @Binds
    @Singleton
    abstract fun bindCropDoctorRepository(impl: CropDoctorRepositoryImpl): CropDoctorRepository
}
