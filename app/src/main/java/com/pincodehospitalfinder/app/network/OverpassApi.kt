package com.pincodehospitalfinder.app.network

import retrofit2.http.GET
import retrofit2.http.Query

data class OverpassResponse(
    val elements: List<OverpassElement>
)

data class OverpassCenter(
    val lat: Double,
    val lon: Double
)

data class OverpassElement(
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OverpassCenter? = null,
    val tags: Map<String, String>? = null
)

interface OverpassApi {
    @GET("interpreter")
    suspend fun getNearbyHospitals(
        @Query("data") query: String
    ): OverpassResponse
}
