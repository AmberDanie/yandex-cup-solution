package com.example.yandex_cup_solution.di

import com.example.yandex_cup_solution.data.CanvasRepository
import com.example.yandex_cup_solution.data.CanvasRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun bindRepository(impl: CanvasRepositoryImpl): CanvasRepository
}
