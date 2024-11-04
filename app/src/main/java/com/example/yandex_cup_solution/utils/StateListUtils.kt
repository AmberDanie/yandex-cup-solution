package com.example.yandex_cup_solution.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import com.example.yandex_cup_solution.domain.CanvasMode
import com.example.yandex_cup_solution.ui.CanvasFigure
import com.example.yandex_cup_solution.ui.CanvasFiguresData
import com.example.yandex_cup_solution.ui.CircleData
import com.example.yandex_cup_solution.ui.PathData
import com.example.yandex_cup_solution.ui.SquareData
import com.example.yandex_cup_solution.ui.TriangleData

fun SnapshotStateList<CanvasFiguresData>.addFigure(
    chosenFigure: CanvasFigure?,
    tempOffset: Offset,
    color: Color,
    lineWidth: Float,
    alpha: Float
) {
    when (chosenFigure) {
        CanvasFigure.Square -> addRect(
            offset = tempOffset,
            color = color,
            lineWidth = lineWidth,
            alpha = alpha
        )

        CanvasFigure.Circle -> addCircle(
            offset = tempOffset,
            color = color,
            lineWidth = lineWidth,
            alpha = alpha
        )

        CanvasFigure.Triangle -> addTriangle(
            offset = tempOffset,
            color = color,
            lineWidth = lineWidth,
            alpha = alpha
        )

        null -> {}
    }
}

fun SnapshotStateList<CanvasFiguresData>.addPath(
    path: Path,
    color: Color,
    lineWidth: Float,
    currentMode: CanvasMode,
    alpha: Float
) {
    add(
        PathData(
            path = path,
            color = color,
            alpha = alpha,
            lineWidth = lineWidth,
            cap = currentMode.toStrokeCap() ?: StrokeCap.Butt
        )
    )
}

fun SnapshotStateList<CanvasFiguresData>.addRect(
    offset: Offset,
    color: Color,
    lineWidth: Float,
    alpha: Float = 0.5f
) {
    add(
        SquareData(offset, color, lineWidth, alpha)
    )
}

fun SnapshotStateList<CanvasFiguresData>.addCircle(
    offset: Offset,
    color: Color,
    lineWidth: Float,
    alpha: Float = 0.5f
) {
    add(
        CircleData(offset, color, lineWidth, alpha)
    )
}

fun SnapshotStateList<CanvasFiguresData>.addTriangle(
    offset: Offset,
    color: Color,
    lineWidth: Float,
    alpha: Float = 0.5f
) {
    add(
        TriangleData(offset, color, lineWidth, alpha)
    )
}