package com.ucas.weatherearthquakemap.data.remote

import com.ucas.weatherearthquakemap.data.model.earthquake.EarthquakeResponse
import com.ucas.weatherearthquakemap.util.Constants.EARTHQUAKE_ENDPOINT
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApi {
   @GET(EARTHQUAKE_ENDPOINT)
    suspend fun getEarthquakeByDate(
        @Query("format") format: String = "geojson",
        @Query("starttime") startTime: String,
        @Query("endtime") endTime: String,
    ): EarthquakeResponse

    @GET(EARTHQUAKE_ENDPOINT)
    suspend fun getEarthquakeByMagnitudeAndDate(
        @Query("format") format: String = "geojson",
        @Query("starttime") startTime: String,
        @Query("endtime") endTime: String,
        @Query("minmagnitude") minMagnitude: Double
    ): EarthquakeResponse
}