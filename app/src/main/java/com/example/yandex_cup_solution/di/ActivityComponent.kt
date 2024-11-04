package com.example.yandex_cup_solution.di

import com.example.yandex_cup_solution.MainActivity
import com.example.yandex_cup_solution.ui.CanvasViewModel
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface ActivityComponent {
    val canvasViewModelFactory: CanvasViewModel.Factory

    fun inject(activity: MainActivity)
}
