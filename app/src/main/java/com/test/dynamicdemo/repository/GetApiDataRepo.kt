package com.test.dynamicdemo.repository

import com.test.dynamicdemo.util.DemoService
import com.test.dynamicdemo.util.SafeApiRequestApproach

class GetApiDataRepo(private val service: DemoService) : SafeApiRequestApproach() {
    suspend fun getApiData() = apiRequest {
        service.getMockApi()
    }
}