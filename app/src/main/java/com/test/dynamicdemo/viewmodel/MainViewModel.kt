package com.test.dynamicdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.dynamicdemo.model.ApiResponse
import com.test.dynamicdemo.model.SurveyData
import com.test.dynamicdemo.repository.GetApiDataRepo
import com.test.dynamicdemo.util.Coroutines
import kotlinx.coroutines.Job

class MainViewModel(private val apiDataRepo: GetApiDataRepo) : ViewModel() {
    private lateinit var getApiDataJob: Job
    private val failedMessage = MutableLiveData<String>()
    private val responseData = MutableLiveData<ApiResponse<Array<SurveyData>>>()



    fun getFailureMessage(): LiveData<String> {
        return failedMessage
    }


    fun getResponseData(): LiveData<ApiResponse<Array<SurveyData>>> {
        return responseData
    }


    fun getApiData() {
        getApiDataJob = Coroutines.ioThenMain({
            apiDataRepo.getApiData()
        }, {
            responseData.value = it
        }, {
            apiException(it!!)
        })
    }


    private fun apiException(e: String) {
        failedMessage.value = e
    }


    override fun onCleared() {
        super.onCleared()
        if (::getApiDataJob.isInitialized) getApiDataJob.cancel()
    }

}