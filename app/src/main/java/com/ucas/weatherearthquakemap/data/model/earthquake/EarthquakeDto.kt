package com.ucas.weatherearthquakemap.data.model.earthquake

import com.google.gson.annotations.SerializedName

// Top-level data class representing a full Earthquake response item
data class EarthquakeDto(
    val properties: Properties,  // Describes the details of the earthquake
    val geometry: Geometry,      // Contains coordinates (longitude, latitude, depth)
    val id: String               // Unique ID for the earthquake event
)

// Contains information about the earthquake itself
data class Properties(
    val mag: Double?,            // Magnitude of the earthquake
    val place: String?,          // Location description (e.g., "Macquarie Island region")
    val time: Long,             // Time of the event in milliseconds since epoch
    val updated: Long?,          // Last updated timestamp
    val tz: Int?,                // Timezone offset (can be null)
    val url: String?,            // URL to more details on USGS
    val detail: String?,         // URL to the API detail endpoint
    val felt: Int?,              // Number of reports from people who felt it
    val cdi: Double?,            // Community Internet Intensity Map value
    val mmi: Double?,            // Modified Mercalli Intensity (shaking level)
    val alert: String?,          // Alert level (e.g., "green", "yellow")
    val status: String?,         // Status of the review (e.g., "reviewed")
    val tsunami: Int?,           // 1 if tsunami is possible, 0 otherwise
    val sig: Int?,               // Significance score
    val net: String?,            // Network ID (e.g., "us")
    val code: String?,           // Code within the network
    val ids: String?,            // Comma-separated list of event IDs
    val sources: String?,        // Comma-separated sources of information
    val types: String?,          // Types of data available (e.g., shakemap, dyfi)
    val nst: Int?,               // Number of seismic stations used
    val dmin: Double?,           // Distance to nearest station
    val rms: Double?,            // Root Mean Square of amplitude
    @SerializedName("gap")
    val gap: Double?,               // Azimuthal gap
    val magType: String?,        // Type of magnitude (e.g., "mww")
    val type: String?,           // Event type (usually "earthquake")
    val title: String?           // Full title/description
)

// Contains the location coordinates
data class Geometry(
    val type: String?,           // Should be "Point" for single location
    val coordinates: List<Double> // [longitude, latitude, depth]
)

