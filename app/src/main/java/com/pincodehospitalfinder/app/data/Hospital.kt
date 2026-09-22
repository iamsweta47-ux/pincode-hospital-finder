package com.pincodehospitalfinder.app.data

data class Hospital(
    val name: String,
    val address: String,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val specialty: String? = null,
    val openingHours: String? = null
)
