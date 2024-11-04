package com.example.yandex_cup_solution.domain

sealed interface FrameInteraction {
    class Delete(val deleteInteraction: DeleteInteraction?) : FrameInteraction
    data object Add : FrameInteraction
    data object Duplicate : FrameInteraction
}