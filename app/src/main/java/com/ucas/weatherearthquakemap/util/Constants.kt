package com.ucas.weatherearthquakemap.util

import android.Manifest

object Constants {
    const val EARTHQUAKE_BASE_URL = "https://earthquake.usgs.gov/"
    const val EARTHQUAKE_ENDPOINT = "fdsnws/event/1/query"
    const val EARTHQUAKE_TYPE = "EARTHQUAKE"
    const val WEATHER_TYPE = "WEATHER"
    const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    const val WEATHER_API_KEY = "13691d30df30a1a0b5eb511c4e45c294"
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
    const val WEATHER_ENDPOINT = "data/2.5/weather"
}