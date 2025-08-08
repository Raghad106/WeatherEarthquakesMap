package com.ucas.weatherearthquakemap.screen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucas.weatherearthquakemap.data.model.earthquake.EarthquakeResponse
import com.ucas.weatherearthquakemap.data.model.weather.WeatherResponse
import com.ucas.weatherearthquakemap.domain.repository.EarthquakeRepository
import com.ucas.weatherearthquakemap.domain.repository.WeatherRepository
import com.ucas.weatherearthquakemap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarthquakeViewModel @Inject constructor(
    private val earthquakeRepository: EarthquakeRepository,
    private val weatherRepository: WeatherRepository
): ViewModel(){
    private val _earthquakeState = MutableLiveData<Resource<EarthquakeResponse>>()
    val earthquakeState: LiveData<Resource<EarthquakeResponse>> = _earthquakeState
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _weatherState = MutableLiveData<Resource<WeatherResponse>>()
    val weatherState: LiveData<Resource<WeatherResponse>> = _weatherState

    fun fetchEarthquakesByDate(starDate: String, endDate: String){
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val result = earthquakeRepository.getEarthquakeByDate(starDate, endDate)
                _earthquakeState.postValue(result)
            } catch (e: Exception) {
                Log.e("Earthquake", "Failed to load: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun fetchEarthquakesByMagAndDate(mag: Double, startDate: String, endDate: String){
        viewModelScope.launch(Dispatchers.IO){
            _isLoading.postValue(true)
            try {
                val result = earthquakeRepository.getEarthquakeByMagnitudeAndDate( startDate,endDate, mag)
                _earthquakeState.postValue(result)
            } catch (e: Exception){
                Log.e("Earthquake", "Failed to load: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getWeatherByLatAndLon(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try{
                val result = weatherRepository.getWeatherByLatAndLon(lat, lon)
                _weatherState.postValue(result)
            } catch (e: Exception){
                Log.e("Earthquake", "Failed to load: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}