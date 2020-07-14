package com.test.dynamicdemo.util

import com.test.dynamicdemo.model.ApiResponse
import com.test.dynamicdemo.model.SurveyData
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface DemoService {


    @GET("data/")
    suspend fun getMockApi(): Response<ApiResponse<Array<SurveyData>>>


    companion object {
        operator fun invoke(): DemoService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor {
                    val originalRequest = it.request()
                    val builder =
                        originalRequest.newBuilder()
                            .addHeader("Content-Type", "application/json")
                    val newRequest = builder.build()
                    it.proceed(newRequest)
                }
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://5f0b3a919d1e150016b372f1.mockapi.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DemoService::class.java)
        }
    }

}