package com.example.yandex_cup_solution.utils

import androidx.compose.ui.graphics.StrokeCap
import com.example.yandex_cup_solution.domain.CanvasMode

fun CanvasMode.toStrokeCap(): StrokeCap? {
    return when (this) {
        CanvasMode.PaintMode.Pencil -> StrokeCap.Round
        CanvasMode.PaintMode.Brush -> StrokeCap.Butt
        CanvasMode.PaintMode.Eraser -> StrokeCap.Square
        else -> null
    }
}
