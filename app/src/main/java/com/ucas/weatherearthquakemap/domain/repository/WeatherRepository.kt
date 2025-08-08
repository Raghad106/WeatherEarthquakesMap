package com.ucas.weatherearthquakemap.domain.repository

import com.ucas.weatherearthquakemap.data.model.weather.WeatherResponse
import com.ucas.weatherearthquakemap.util.Constants
import com.ucas.weatherearthquakemap.util.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRepository {
    suspend fun getWeatherByLatAndLon(
        lat: Double, lon: Double, ): Resource<WeatherResponse>
}