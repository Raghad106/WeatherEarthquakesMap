package com.ucas.weatherearthquakemap.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ucas.weatherearthquakemap.R
import com.ucas.weatherearthquakemap.data.model.earthquake.EarthquakeDto
import com.ucas.weatherearthquakemap.data.model.weather.WeatherResponse
import com.ucas.weatherearthquakemap.databinding.FragmentMapHostBinding
import com.ucas.weatherearthquakemap.util.Constants
import com.ucas.weatherearthquakemap.util.Constants.EARTHQUAKE_TYPE
import com.ucas.weatherearthquakemap.util.Constants.WEATHER_TYPE
import com.ucas.weatherearthquakemap.util.HelpingFunction
import com.ucas.weatherearthquakemap.util.HelpingFunction.getEndOfDay
import com.ucas.weatherearthquakemap.util.HelpingFunction.getEndOfYesterday
import com.ucas.weatherearthquakemap.util.HelpingFunction.getStartOfDay
import com.ucas.weatherearthquakemap.util.HelpingFunction.getStartOfMonth
import com.ucas.weatherearthquakemap.util.HelpingFunction.getStartOfThisWeek
import com.ucas.weatherearthquakemap.util.HelpingFunction.getStartOfYesterday
import com.ucas.weatherearthquakemap.util.HelpingFunction.isConnected
import com.ucas.weatherearthquakemap.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class FragmentMapHost : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapHostBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var buttonType: String
    private val viewModel: EarthquakeViewModel by viewModels()
    private lateinit var weather: WeatherResponse
    private var loadingDialog: LoadingDialog? = null
    private var selectedMarker: Marker? = null
    private val markerQuakeMap = mutableMapOf<Marker, EarthquakeDto>()
    private var startDate = getStartOfDay()
    private var endDate = getEndOfDay()
    private var mag: Double? = -1.0
    private val networkViewModel: NetworkViewModel by activityViewModels()
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private val multipleLocationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
        val isFineGrunted = permissions[Constants.FINE_LOCATION_PERMISSION]  ?: false
        val isCoarseGrunted = permissions[Constants.COARSE_LOCATION_PERMISSION]  ?: false
        val isBackgroundGrunted = permissions[Constants.BACKGROUND_LOCATION_PERMISSION]  ?: false

        if (isFineGrunted || isBackgroundGrunted || isCoarseGrunted)
            getUserLocation()
        else
            Toast.makeText(requireContext(), "Location permission is denied", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapHostBinding.inflate(inflater, container, false)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        buttonType = WEATHER_TYPE
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkPermission()
        networkViewModel.isConnected.observe(viewLifecycleOwner) @androidx.annotation.RequiresPermission(
                allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]
            ) { isConnected ->
            if (isConnected) {
                mapFragment.getMapAsync(this)
                binding.btnWeather.visibility = View.VISIBLE
                binding.btnEarthquake.visibility = View.VISIBLE
            } else {
                hideViews()
                Toast.makeText(requireContext(), "You need internet connection", Toast.LENGTH_LONG).show()
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    if (loadingDialog == null) {
                        loadingDialog = LoadingDialog()
                        loadingDialog?.show(parentFragmentManager, "loading")
                    }
                } else {
                    loadingDialog?.dismiss()
                    loadingDialog = null
                }
            }
            viewModel.earthquakeState.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("Earthquakes", "Loading")
                        map.clear()
                    }

                    is Resource.Success -> {
                        val earthquakes: List<EarthquakeDto> = resource.data.features
                        addEarthquakeMarkers(earthquakes)
                        binding.cardAllEarthquakesInf.visibility = View.VISIBLE
                        binding.textEarthquakeCount.text = earthquakes.size.toString()
                        Log.d("Earthquakes", "Size: ${earthquakes.size}")
                        Log.d("Earthquakes", "Done")
                    }

                    is Resource.Error -> {
                        if(!isConnected(requireContext())){
                            Toast.makeText(requireContext(), "You need internet connection", Toast.LENGTH_LONG).show()
                        }
                        Log.d("Earthquakes", "Error: ${resource.message}")
                    }
                }
            }
            viewModel.weatherState.observe(viewLifecycleOwner){resource ->
                when (resource){
                    is Resource.Loading -> {
                        Log.d("WeatherLOG", "Loading")
                        map.clear()
                    }
                    is Resource.Success -> {
                        weather = resource.data
                        binding.cardWeatherInfo.visibility = View.VISIBLE
                        if (weather != null) {
                            binding.textTemperature.text = "${(weather.main.temp - 273.15).toInt()}°"
                            binding.textDescription.text = weather.weather[0].description
                            binding.textHumidity.text = "${weather.main.humidity}%"
                            binding.textLocationTemperature.text = weather.sys.country
                        }
                    }
                    is Resource.Error -> {
                        if(!isConnected(requireContext())){
                            Toast.makeText(requireContext(), "You need internet connection", Toast.LENGTH_LONG).show()
                        }
                        Log.d("WeatherLOG", "Error: ${resource.message}")
                    }
                }
            }
            changeVisibility()

            val spinner: Spinner = binding.spinnerDateFilter
            val filters = resources.getStringArray(R.array.date_filters)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filters)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selected = filters[position]
                    when (selected) {
                        "Today" -> {
                            startDate = getStartOfDay()
                            endDate = getEndOfDay()
                        }
                        "Yesterday" -> {
                            startDate = getStartOfYesterday()
                            endDate = getEndOfYesterday()
                        }
                        "Last Week" -> {
                            startDate = getStartOfThisWeek()
                            endDate = getStartOfDay()

                        }
                        "Last Month" -> {
                            startDate = getStartOfMonth()
                            endDate = getStartOfDay()
                        }
                        "Choose Date" -> {
                            showDatePicker()
                        }
                    }

                    chooseAppropriateFilter()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            binding.etMagnitudeSearch.setOnClickListener { view ->
                val userInput  = binding.etMagnitudeSearch.text?.toString()?.trim()
                mag = userInput?.toDoubleOrNull()
                if (mag != null){
                    filterByDateRange(mag!!, startDate, endDate)
                    mag = -1.0
                } else {
                    binding.etMagnitudeSearch.error = "Enter a valid number"
                }
            }
            binding.btnEarthquake.setOnClickListener { view ->
                changeVisibility(false)
                buttonType = EARTHQUAKE_TYPE
                filterByDateRange(getStartOfDay(), getEndOfDay())
            }
            binding.btnWeather.setOnClickListener  { view ->
                changeVisibility()
                buttonType = WEATHER_TYPE
                checkPermission()
            }
            binding.ibMyLoction.setOnClickListener { view ->
                map.clear()
                getUserLocation()
            }
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkPermission()
        map.isMyLocationEnabled = true

        googleMap.setOnMapClickListener { latLng ->
            map.clear()
            showMarkerOnMap(latLng.latitude, latLng.longitude)
        }
    }

    private fun changeVisibility(isWeather: Boolean = true){
        if (isWeather){
            binding.btnWeather.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_dark))
            binding.btnEarthquake.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_light))
            binding.cardWeatherInfo.visibility = View.VISIBLE
            binding.spinnerDateFilter.visibility = View.GONE
            binding.etMagnitudeSearch.visibility = View.GONE
            binding.cardEarthquakeInfo.visibility = View.GONE
            binding.cardAllEarthquakesInf.visibility = View.GONE
            binding.magnitudeInputLayout.visibility = View.GONE
            binding.ibMyLoction.visibility = View.VISIBLE
        }
        else{
            binding.btnEarthquake.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_dark))
            binding.btnWeather.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue_light))
            binding.cardWeatherInfo.visibility = View.GONE
            binding.spinnerDateFilter.visibility = View.VISIBLE
            binding.etMagnitudeSearch.visibility = View.VISIBLE
            binding.cardAllEarthquakesInf.visibility = View.VISIBLE
            binding.magnitudeInputLayout.visibility = View.VISIBLE
            binding.ibMyLoction.visibility = View.GONE
        }
    }
    @SuppressLint("SetTextI18n")
    private fun addEarthquakeMarkers(earthquakes: List<EarthquakeDto>) {
        map.clear()
        markerQuakeMap.clear()

        for (quake in earthquakes) {
            val icon = HelpingFunction.getColoredMarkerIcon(
                requireContext(),
                R.drawable.ic_marker,
                R.color.blue_dark
            )
            val position = quake.geometry.coordinates
            if (position.size >= 2) {
                val latLng = LatLng(position[1], position[0])
                val title = quake.properties.title ?: "Earthquake"
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
                val marker = map.addMarker(MarkerOptions().position(latLng).title(title).icon(icon))

                if (marker != null) {
                    markerQuakeMap[marker] = quake
                }
            }
        }

        map.setOnMarkerClickListener { marker ->
            if (selectedMarker == marker) {
                // Re-selected the same marker → hide the card
                binding.cardEarthquakeInfo.visibility = View.GONE
                selectedMarker = null
            } else {
                // New marker selected or first time
                val quake = markerQuakeMap[marker]
                Log.d("MarkerLog", marker.toString())
                if (quake != null) {
                    binding.cardEarthquakeInfo.visibility = View.VISIBLE
                    binding.textMagnitude.text = quake.properties.mag.toString()
                    binding.textLocation.text = quake.properties.place ?: "Unknown Location"
                    binding.textDepth.text = quake.properties.gap.toString() + " km"

                    val timestamp: Long = quake.properties.time
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
                        .withZone(ZoneId.systemDefault())
                    val formattedDate = formatter.format(Instant.ofEpochMilli(timestamp))
                    binding.textDateTime.text = formattedDate
                    selectedMarker = marker
                }
                selectedMarker = marker
            }
            true // consume the event
        }

        Log.d("Earthquakes", "Markers added")
    }
    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val customCal = Calendar.getInstance()
            customCal.set(year, month, dayOfMonth, 0, 0, 0)
            val start = customCal.time
            // Add one day to get the end date
            customCal.add(Calendar.DAY_OF_MONTH, 1)
            val end = customCal.time
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            startDate = formatter.format(start)
            endDate = formatter.format(end)
            chooseAppropriateFilter()

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }
    private fun filterByDateRange(startMillis: String, endMillis: String) {
        viewModel.fetchEarthquakesByDate(startMillis, endMillis)
    }
    private fun filterByDateRange(mag: Double, startMillis: String, endMillis: String) {
        viewModel.fetchEarthquakesByMagAndDate(mag, startMillis, endMillis)
    }
    @SuppressLint("SetTextI18n")
    private fun chooseAppropriateFilter(){
        mag?.takeIf { it > 0.0 }?.let {
            filterByDateRange(it, startDate, endDate)
        } ?: run {
            filterByDateRange(startDate, endDate)
            binding.etMagnitudeSearch.setText("")
            binding.etMagnitudeSearch.setText("")
            binding.etMagnitudeSearch.clearFocus()
            binding.cardEarthquakeInfo.visibility = View.GONE
        }
        binding.textStart.text = "start at: $startDate"
        binding.textEnd.text = "end at: $endDate"
    }
    private fun hideViews(){
        binding.btnWeather.visibility = View.GONE
        binding.btnEarthquake.visibility = View.GONE
        binding.spinnerDateFilter.visibility = View.GONE
        binding.etMagnitudeSearch.visibility = View.GONE
        binding.cardEarthquakeInfo.visibility = View.GONE
        binding.cardAllEarthquakesInf.visibility = View.GONE
        binding.magnitudeInputLayout.visibility = View.GONE
    }

    private fun showMarkerOnMap(lat: Double, lon: Double) {
        viewModel.getWeatherByLatAndLon(lat, lon)
        val latLng = LatLng(lat, lon)
        val icon = HelpingFunction.getColoredMarkerIcon(
            requireContext(),
            R.drawable.ic_marker,
            R.color.blue_dark
        )
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(icon)
        )
        Log.d("WeatherLog", "Add Marker")
        Log.d("WeatherLog", "Zoom")
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun checkPermission(){
        if(HelpingFunction.hasLocationPermission(requireContext())){
           // map.isMyLocationEnabled = true
            getUserLocation()
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                multipleLocationPermissions.launch(arrayOf(
                    Constants.FINE_LOCATION_PERMISSION,
                    Constants.COARSE_LOCATION_PERMISSION,
                ))
            else
                multipleLocationPermissions.launch(arrayOf(
                    Constants.FINE_LOCATION_PERMISSION,
                    Constants.COARSE_LOCATION_PERMISSION,
                    Constants.BACKGROUND_LOCATION_PERMISSION,
                ))
        }
    }
    @SuppressLint("MissingPermission")
    fun getUserLocation(){
        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                val latLng: LatLng = LatLng(lat, lon)
                showMarkerOnMap(lat, lon)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            } else
                Toast.makeText(requireContext(), "Open your location", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}