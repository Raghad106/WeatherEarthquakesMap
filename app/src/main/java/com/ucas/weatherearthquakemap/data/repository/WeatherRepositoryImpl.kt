package com.ucas.weatherearthquakemap.data.repository

import com.ucas.weatherearthquakemap.data.model.weather.WeatherResponse
import com.ucas.weatherearthquakemap.data.remote.WeatherApi
import com.ucas.weatherearthquakemap.domain.repository.WeatherRepository
import com.ucas.weatherearthquakemap.util.Resource
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
): WeatherRepository {
    override suspend fun getWeatherByLatAndLon(
        lat: Double,
        lon: Double,
    ): Resource<WeatherResponse> {
        return try {
            val response =  weatherApi.getWeatherByLatAndLon(lat = lat, lon = lon)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }
}