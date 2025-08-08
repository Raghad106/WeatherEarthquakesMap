package com.ucas.weatherearthquakemap.domain.repository

import com.ucas.weatherearthquakemap.data.model.earthquake.EarthquakeResponse
import com.ucas.weatherearthquakemap.util.Resource

interface EarthquakeRepository {

    suspend fun getEarthquakeByDate(
       startTime: String,
       endTime: String,
    ): Resource<EarthquakeResponse>

    suspend fun getEarthquakeByMagnitudeAndDate(
        startTime: String,
        endTime: String,
        minMagnitude: Double
    ): Resource<EarthquakeResponse>
}