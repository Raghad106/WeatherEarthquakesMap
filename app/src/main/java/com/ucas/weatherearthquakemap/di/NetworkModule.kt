package com.ucas.weatherearthquakemap.di

import com.ucas.weatherearthquakemap.data.remote.EarthquakeApi
import com.ucas.weatherearthquakemap.data.remote.WeatherApi
import com.ucas.weatherearthquakemap.util.Constants.EARTHQUAKE_BASE_URL
import com.ucas.weatherearthquakemap.util.Constants.WEATHER_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("earthquake")
    fun provideBaseUrl() = EARTHQUAKE_BASE_URL

    @Provides
    @Named("weather")
    fun provideWeatherUrl() = WEATHER_BASE_URL

    @Singleton
    @Provides
    fun provideEarthquakeApi(@Named("earthquake") baseUrl: String): EarthquakeApi{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EarthquakeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideWeatherApi(@Named("weather") baseUrl: String): WeatherApi{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}