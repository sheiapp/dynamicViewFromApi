package com.test.dynamicdemo.util

import com.test.dynamicdemo.repository.GetApiDataRepo
import com.test.dynamicdemo.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dependencyModule = module {
    single { DemoService() }
    single { GetApiDataRepo(get()) }
    viewModel { MainViewModel(get()) }
}