package com.ucas.weatherearthquakemap.data.remote

import android.provider.SyncStateContract
import com.ucas.weatherearthquakemap.data.model.weather.WeatherResponse
import com.ucas.weatherearthquakemap.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET(Constants.WEATHER_ENDPOINT)
    suspend fun getWeatherByLatAndLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = "13691d30df30a1a0b5eb511c4e45c294"): WeatherResponse
}