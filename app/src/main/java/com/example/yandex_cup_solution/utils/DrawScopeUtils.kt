package com.example.yandex_cup_solution.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.yandex_cup_solution.ui.CircleData
import com.example.yandex_cup_solution.ui.PathData
import com.example.yandex_cup_solution.ui.SquareData
import com.example.yandex_cup_solution.ui.TriangleData

fun DrawScope.drawPath(figureData: PathData) {
    drawPath(
        path = figureData.path,
        color = figureData.color,
        style = Stroke(
            width = figureData.lineWidth,
            cap = figureData.cap
        )
    )
}

fun DrawScope.drawSquare(figureData: SquareData) {
    drawRoundRect(
        color = figureData.color,
        style = Stroke(
            width = figureData.lineWidth / 2f
        ),
        size = Size(figureData.lineWidth * 10, figureData.lineWidth * 10),
        topLeft = figureData.offset,
        alpha = figureData.alpha
    )
}

fun DrawScope.drawCircle(figureData: CircleData) {
    drawCircle(
        color = figureData.color,
        style = Stroke(
            width = figureData.lineWidth / 2f
        ),
        radius = figureData.lineWidth * 4f,
        center = figureData.offset,
        alpha = figureData.alpha
    )
}

fun DrawScope.drawTriangle(figureData: TriangleData) {
    val center = figureData.offset
    val size = Size(figureData.lineWidth, figureData.lineWidth)
    val path = Path().apply {
        moveTo(center.x, center.y - size.height * 8)
        lineTo(center.x + size.width * 5, center.y - size.height)
        lineTo(center.x - size.width * 5, center.y - size.height)
        close()
    }
    drawPath(
        path = path,
        color = figureData.color,
        style = Stroke(
            width = figureData.lineWidth / 1.5f
        ),
        alpha = figureData.alpha
    )
}