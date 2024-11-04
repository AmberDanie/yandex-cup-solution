package com.example.yandex_cup_solution

import android.app.Application
import com.example.yandex_cup_solution.di.DaggerAppComponent

class YandexCupApplication : Application() {
    val appComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }
}