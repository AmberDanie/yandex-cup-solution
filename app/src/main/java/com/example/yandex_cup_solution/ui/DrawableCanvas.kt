package com.example.yandex_cup_solution.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.CanvasMode
import com.example.yandex_cup_solution.utils.addFigure
import com.example.yandex_cup_solution.utils.addPath
import com.example.yandex_cup_solution.utils.drawCircle
import com.example.yandex_cup_solution.utils.drawPath
import com.example.yandex_cup_solution.utils.drawSquare
import com.example.yandex_cup_solution.utils.drawTriangle

@Composable
fun DrawableCanvas(
    currentMode: CanvasMode,
    lineWidth: Float,
    cleanStack: () -> Unit,
    chosenColor: Color,
    chosenFigure: CanvasFigure?,
    onUpdateFrameFigures: (SnapshotStateList<CanvasFiguresData>) -> Unit,
    previousFrame: SnapshotStateList<CanvasFiguresData>,
    currentFrame: SnapshotStateList<CanvasFiguresData>,
    modifier: Modifier = Modifier,
) {
    var tempPath = Path()

    var tempOffset = Offset.Zero

    val color = if (currentMode is CanvasMode.PaintMode.Eraser) {
        colorResource(id = R.color.white)
    } else {
        chosenColor
    }

    Canvas(modifier = modifier
        .fillMaxWidth()
        .background(colorResource(id = R.color.white))
        .clipToBounds()
        .pointerInput(chosenColor, lineWidth, currentMode, chosenFigure, currentFrame) {
            detectDragGestures(
                onDragStart = {
                    tempPath = Path()
                    tempOffset = Offset.Zero
                    cleanStack()
                },
                onDragEnd = {
                    if (currentFrame.isNotEmpty()) {
                        val lastAdded = currentFrame.last()
                        if ((lastAdded is CanvasInstrument && lastAdded.alpha == 0.5f)
                            || (lastAdded is PathData && lastAdded.alpha == 0.5f)
                        ) {
                            currentFrame.removeLast()
                        }
                    }
                    when (currentMode) {
                        is CanvasMode.PaintMode -> {
                            currentFrame.addPath(tempPath, color, lineWidth, currentMode, 1f)
                        }

                        CanvasMode.Instruments -> {
                            currentFrame.addFigure(
                                chosenFigure,
                                tempOffset,
                                color,
                                lineWidth,
                                1f
                            )
                        }

                        CanvasMode.ColorPicker,
                        CanvasMode.Disabled -> { /* IMPOSSIBLE STATE */ }
                    }
                    onUpdateFrameFigures(currentFrame)
                }
            ) { change, dragAmount ->

                val changeX = change.position.x
                val changeY = change.position.y

                tempPath.moveTo(changeX - dragAmount.x, changeY - dragAmount.y)
                tempPath.lineTo(changeX, changeY)

                tempOffset = Offset(changeX, changeY)

                if (currentFrame.isNotEmpty()) {
                    val lastAdded = currentFrame.last()
                    if ((lastAdded is CanvasInstrument && lastAdded.alpha == 0.5f)
                        || (lastAdded is PathData && lastAdded.alpha == 0.5f)
                    ) {
                        currentFrame.removeLast()
                    }
                }

                when (currentMode) {
                    is CanvasMode.PaintMode -> {
                        currentFrame.addPath(tempPath, color, lineWidth, currentMode, 0.5f)
                    }

                    CanvasMode.Instruments -> {
                        currentFrame.addFigure(chosenFigure, tempOffset, color, lineWidth, 0.5f)
                    }

                    CanvasMode.ColorPicker,
                    CanvasMode.Disabled -> { /* IMPOSSIBLE STATE */ }
                }
            }
        }
    ) {
        previousFrame.forEach { figureData ->
            when (figureData) {
                is PathData -> drawPath(figureData, alphaOverride = 0.5f)
                is SquareData -> drawSquare(figureData, alphaOverride = 0.5f)
                is CircleData -> drawCircle(figureData, alphaOverride = 0.5f)
                is TriangleData -> drawTriangle(figureData, alphaOverride = 0.5f)
            }
        }

        currentFrame.forEach { figureData ->
            when (figureData) {
                is PathData -> drawPath(figureData)
                is SquareData -> drawSquare(figureData)
                is CircleData -> drawCircle(figureData)
                is TriangleData -> drawTriangle(figureData)
            }
        }
    }
}
