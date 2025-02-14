package com.example.singnature.WildlifeMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singnature.Network.speciesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpeciesViewModel : ViewModel() {

    private val _speciesList = MutableLiveData<List<Species>>(emptyList())
    val speciesList: LiveData<List<Species>> get() = _speciesList

    private val _speciesDetail = MutableLiveData<Species?>()
    val speciesDetail: LiveData<Species?> get() = _speciesDetail

    fun fetchSpeciesByCategory(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val speciesResponse = speciesApiService.getSpeciesByCategory(categoryId).execute()

                if (speciesResponse.isSuccessful) {
                    _speciesList.postValue(speciesResponse.body() ?: emptyList())
                } else {
                    println("ERROR: API call failed - Species: ${speciesResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to fetch species by category - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchSpeciesDetail(specieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val speciesResponse = speciesApiService.getSpeciesById(specieId).execute()

                if (speciesResponse.isSuccessful) {
                    // Post value to main thread
                    _speciesDetail.postValue(speciesResponse.body())
                } else {
                    println("ERROR: Failed to fetch species detail - ${speciesResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Exception fetching species detail - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
