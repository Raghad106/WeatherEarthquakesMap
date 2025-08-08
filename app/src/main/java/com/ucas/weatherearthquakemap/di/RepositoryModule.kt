package com.ucas.weatherearthquakemap.di

import com.ucas.weatherearthquakemap.domain.repository.EarthquakeRepository
import com.ucas.weatherearthquakemap.data.repository.EarthquakeRepositoryImpl
import com.ucas.weatherearthquakemap.data.repository.WeatherRepositoryImpl
import com.ucas.weatherearthquakemap.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEarthquakeRepository(
        impl: EarthquakeRepositoryImpl
    ): EarthquakeRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}
