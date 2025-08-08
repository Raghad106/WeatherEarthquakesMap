package com.ucas.weatherearthquakemap.data.repository

import android.util.Log
import com.ucas.weatherearthquakemap.data.model.earthquake.EarthquakeResponse
import com.ucas.weatherearthquakemap.data.remote.EarthquakeApi
import com.ucas.weatherearthquakemap.domain.repository.EarthquakeRepository
import com.ucas.weatherearthquakemap.util.Resource
import javax.inject.Inject

class EarthquakeRepositoryImpl @Inject constructor(
    private val earthquakeApi: EarthquakeApi
) : EarthquakeRepository{


    override suspend fun getEarthquakeByDate(
        startTime: String,
        endTime: String,
    ): Resource<EarthquakeResponse> {
        return try {
            val response = earthquakeApi.getEarthquakeByDate(startTime = startTime, endTime = endTime)
            Resource.Success(response)
        }catch (e: Exception){
            Log.e("Earthquakes", "Error: ${e.message}", e)
            Resource.Error(e.localizedMessage?: "Unknown error occurred")
        }
    }

    override suspend fun getEarthquakeByMagnitudeAndDate(
        startTime: String,
        endTime: String,
        minMagnitude: Double,
    ): Resource<EarthquakeResponse> {
        return try {
            val response = earthquakeApi.getEarthquakeByMagnitudeAndDate(startTime = startTime, endTime = endTime, minMagnitude = minMagnitude)
            Resource.Success(response)
        } catch (e: Exception){
            Log.e("Earthquakes", "Error: ${e.message}", e)
            Resource.Error(e.localizedMessage?: "Unknown error occurred")
        }
    }
}