package com.test.dynamicdemo.model

data class ApiResponse<D>(
    val code: String,
    val msg: String,
    val surveyData: D
)