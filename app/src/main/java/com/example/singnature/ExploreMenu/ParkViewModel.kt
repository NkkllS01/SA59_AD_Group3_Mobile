package com.example.singnature.ExploreMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singnature.Network.parkApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParkViewModel : ViewModel() {
    private val _parkList = MutableLiveData<List<Park>>()
    val parkList: LiveData<List<Park>> get() = _parkList

    private val _parkDetail = MutableLiveData<Park>()
    val parkDetail: LiveData<Park> get() = _parkDetail

    fun getAllParks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                parkApiService.getAllParks().enqueue(object : Callback<List<Park>> {
                    override fun onResponse(call: Call<List<Park>>, response: Response<List<Park>>) {
                        if (response.isSuccessful) {
                            _parkList.postValue(response.body())  // Update LiveData with the park list
                        } else {
                            println("ERROR: Failed to fetch park list - ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Park>>, t: Throwable) {
                        println("ERROR: Exception fetching park list - ${t.message}")
                        t.printStackTrace()
                    }
                })
            } catch (e: Exception) {
                println("ERROR: Exception fetching park list - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchParkDetail(parkId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parkResponse = parkApiService.getParkById(parkId).execute()

                if (parkResponse.isSuccessful) {
                    _parkDetail.postValue(parkResponse.body())  // Update LiveData with park details
                } else {
                    println("ERROR: Failed to fetch park detail - ${parkResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Exception fetching park detail - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

