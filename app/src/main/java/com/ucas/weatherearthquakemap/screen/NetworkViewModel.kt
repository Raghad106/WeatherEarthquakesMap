package com.ucas.weatherearthquakemap.screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NetworkViewModel : ViewModel() {
    val isConnected = MutableLiveData<Boolean>()
}