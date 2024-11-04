package com.example.yandex_cup_solution.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap

interface CanvasFiguresData

open class CanvasInstrument(
    open val alpha: Float
)

data class PathData(
    val path: Path,
    val color: Color,
    val alpha: Float,
    val lineWidth: Float,
    val cap: StrokeCap
) : CanvasFiguresData

data class SquareData(
    val offset: Offset,
    val color: Color,
    val lineWidth: Float,
    override val alpha: Float
) : CanvasFiguresData, CanvasInstrument(alpha = alpha)

data class CircleData(
    val offset: Offset,
    val color: Color,
    val lineWidth: Float,
    override val alpha: Float
) : CanvasFiguresData, CanvasInstrument(alpha = alpha)

data class TriangleData(
    val offset: Offset,
    val color: Color,
    val lineWidth: Float,
    override val alpha: Float
) : CanvasFiguresData, CanvasInstrument(alpha = alpha)

sealed interface CanvasFigure {
    data object Triangle : CanvasFigure
    data object Square : CanvasFigure
    data object Circle : CanvasFigure
}
