package com.example.yandex_cup_solution.domain

sealed interface CanvasMode {

    sealed interface PaintMode : CanvasMode {
        data object Pencil : PaintMode
        data object Brush : PaintMode
        data object Eraser : PaintMode
    }

    data object Instruments : CanvasMode

    data object ColorPicker : CanvasMode

    data object Disabled : CanvasMode
}
