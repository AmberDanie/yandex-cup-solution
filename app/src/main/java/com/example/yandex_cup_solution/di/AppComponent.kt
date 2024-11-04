package com.example.yandex_cup_solution.di

import android.content.Context
import com.example.yandex_cup_solution.YandexCupApplication
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [RepositoryModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }

    val activityComponent: ActivityComponent

    fun inject(application: YandexCupApplication)
}
