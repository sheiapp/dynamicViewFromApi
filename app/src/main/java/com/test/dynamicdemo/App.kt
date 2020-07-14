package com.test.dynamicdemo

import android.app.Application
import com.test.dynamicdemo.util.dependencyModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(dependencyModule))
        }
    }
}