package com.example.singnature.WildlifeMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singnature.Network.sightingsApiService
import com.example.singnature.Network.speciesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchKeyword = MutableLiveData<String>()
    val searchKeyword: LiveData<String> get() = _searchKeyword

    private val _speciesList = MutableLiveData<List<Species>>(emptyList())
    val speciesList: LiveData<List<Species>> get() = _speciesList

    private val _sightingsList = MutableLiveData<List<Sightings>>(emptyList())
    val sightingsList: LiveData<List<Sightings>> get() = _sightingsList

    fun searchByKeyword(keyword: String) {

        _searchKeyword.value = keyword

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val speciesResponse = speciesApiService.searchSpeciesByKeyword(keyword).execute()
                val sightingsResponse = sightingsApiService.searchSightingsByKeyword(keyword).execute()

                if (speciesResponse.isSuccessful && sightingsResponse.isSuccessful) {
                    _speciesList.postValue(speciesResponse.body() ?: emptyList())
                    _sightingsList.postValue(sightingsResponse.body() ?: emptyList())
                } else {
                    println("ERROR: API call failed - Species: ${speciesResponse.code()}, Sightings: ${sightingsResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to fetch search results - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}