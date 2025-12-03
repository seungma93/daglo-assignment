package com.seungma.daglo

import android.app.Application
import com.seungma.daglo.di.components.AppComponent
import com.seungma.daglo.di.components.DaggerAppComponent

class DagloApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}
