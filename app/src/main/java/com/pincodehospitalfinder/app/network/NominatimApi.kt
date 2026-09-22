package com.pincodehospitalfinder.app.network

import retrofit2.http.GET
import retrofit2.http.Query

data class NominatimResult(
    val lat: String,
    val lon: String,
    val display_name: String
)

interface NominatimApi {
    @GET("search")
    suspend fun searchPinCode(
        @Query("postalcode") pinCode: String,
        @Query("country") country: String = "India",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<NominatimResult>
}
