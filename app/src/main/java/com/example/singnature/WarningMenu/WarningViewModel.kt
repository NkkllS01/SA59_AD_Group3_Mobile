package com.example.singnature.WarningMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singnature.Network.warningApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WarningViewModel : ViewModel() {
    private val _warningList = MutableLiveData<List<Warning>>()
    val warningList: LiveData<List<Warning>> get() = _warningList

    private val _warningDetail = MutableLiveData<Warning>()
    val warningDetail: LiveData<Warning> get() = _warningDetail

    fun getAllWarnings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                warningApiService.getAllWarnings().enqueue(object: Callback<List<Warning>> {
                    override fun onResponse(p0: Call<List<Warning>>, response: Response<List<Warning>>) {
                        if (response.isSuccessful) {
                            _warningList.postValue(response.body())
                        } else {
                            println("ERROR: Failed to fetch warning list - ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Warning>>, t: Throwable) {
                        println("ERROR: Exception fetching warning list - ${t.message}")
                        t.printStackTrace()
                    }
                })
            } catch (e: Exception) {
                println("ERROR: Exception fetching park list - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchWarningDetail(warningId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Execute the synchronous request
                val warningResponse = warningApiService.getWarningById(warningId).execute()

                if (warningResponse.isSuccessful) {
                    _warningDetail.postValue(warningResponse.body())  // Update LiveData with park details
                } else {
                    println("ERROR: Failed to fetch warning detail - ${warningResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Exception fetching warning detail - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

