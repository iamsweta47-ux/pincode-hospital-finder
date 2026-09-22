package com.pincodehospitalfinder.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pincodehospitalfinder.app.data.Hospital
import com.pincodehospitalfinder.app.repository.HospitalRepository
import com.pincodehospitalfinder.app.repository.HospitalResult
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val hospitals: List<Hospital>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class HospitalViewModel : ViewModel() {

    private val repository = HospitalRepository()

    var searchState by mutableStateOf<SearchState>(SearchState.Idle)
        private set

    private var isSearching = false

    fun searchHospitals(pinCode: String) {
        if (isSearching) return // prevent duplicate requests

        isSearching = true
        searchState = SearchState.Loading

        viewModelScope.launch {
            when (val result = repository.findTopHospitals(pinCode)) {
                is HospitalResult.Success -> searchState = SearchState.Success(result.hospitals)
                is HospitalResult.Error -> searchState = SearchState.Error(result.message)
            }
            isSearching = false
        }
    }
}
