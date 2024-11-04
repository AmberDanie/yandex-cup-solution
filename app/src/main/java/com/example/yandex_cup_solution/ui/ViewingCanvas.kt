package com.example.yandex_cup_solution.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.colorResource
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.utils.drawCircle
import com.example.yandex_cup_solution.utils.drawPath
import com.example.yandex_cup_solution.utils.drawSquare
import com.example.yandex_cup_solution.utils.drawTriangle

@Composable
fun ViewingCanvas(
    modifier: Modifier = Modifier,
    frame: SnapshotStateList<CanvasFiguresData>
) {
    Canvas(modifier = modifier
        .fillMaxWidth()
        .background(colorResource(id = R.color.white))
        .clipToBounds()) {
        frame.forEach { figureData ->
            when (figureData) {
                is PathData -> drawPath(figureData)
                is SquareData -> drawSquare(figureData)
                is CircleData -> drawCircle(figureData)
                is TriangleData -> drawTriangle(figureData)
            }
        }
    }
}