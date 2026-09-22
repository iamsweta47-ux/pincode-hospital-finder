package com.pincodehospitalfinder.app.repository

import com.pincodehospitalfinder.app.data.Hospital
import com.pincodehospitalfinder.app.network.ApiClient
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

sealed class HospitalResult {
    data class Success(val hospitals: List<Hospital>) : HospitalResult()
    data class Error(val message: String) : HospitalResult()
}

class HospitalRepository {

    suspend fun findTopHospitals(pinCode: String): HospitalResult {
        return try {
            val locationResults = ApiClient.nominatimApi.searchPinCode(pinCode)

            if (locationResults.isEmpty()) {
                return HospitalResult.Error("We couldn't find this PIN code. Please try another one.")
            }

            val userLat = locationResults[0].lat.toDouble()
            val userLon = locationResults[0].lon.toDouble()

            val query = """
                [out:json];
                (
                  node["amenity"="hospital"](around:15000,$userLat,$userLon);
                  way["amenity"="hospital"](around:15000,$userLat,$userLon);
                  relation["amenity"="hospital"](around:15000,$userLat,$userLon);
                );
                out center;
            """.trimIndent()

            val response = ApiClient.overpassApi.getNearbyHospitals(query)

            if (response.elements.isEmpty()) {
                return HospitalResult.Error("We couldn't find hospitals near this PIN code. Please try another PIN code.")
            }

            val hospitals = response.elements.mapNotNull { element ->
                val lat = element.lat ?: element.center?.lat ?: return@mapNotNull null
                val lon = element.lon ?: element.center?.lon ?: return@mapNotNull null
                val tags = element.tags ?: emptyMap()
                val name = tags["name"] ?: return@mapNotNull null

                Hospital(
                    name = name,
                    address = tags["addr:full"]
                        ?: listOfNotNull(tags["addr:street"], tags["addr:city"]).joinToString(", ")
                            .ifBlank { "Address unavailable" },
                    distanceKm = calculateDistance(userLat, userLon, lat, lon),
                    latitude = lat,
                    longitude = lon,
                    phone = tags["phone"] ?: tags["contact:phone"],
                    specialty = tags["healthcare:speciality"],
                    openingHours = tags["opening_hours"]
                )
            }
                .distinctBy { it.name }
                .sortedBy { it.distanceKm }
                .take(3)

            if (hospitals.isEmpty()) {
                HospitalResult.Error("We couldn't find hospitals near this PIN code. Please try another PIN code.")
            } else {
                HospitalResult.Success(hospitals)
            }

        } catch (e: Exception) {
            HospitalResult.Error("Something went wrong. Please check your internet connection and try again.")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}
