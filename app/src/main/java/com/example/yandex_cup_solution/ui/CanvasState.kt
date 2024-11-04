package com.example.yandex_cup_solution.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.example.yandex_cup_solution.domain.CanvasMode

data class CanvasState(
    val paletteIsVisible: Boolean = false,
    val paletteIsExpanded: Boolean = false,
    val instrumentsIsExpanded: Boolean = false,
    val deleteDialogIsExpanded: Boolean = false,
    val generateFramesDialogIsExpanded: Boolean = false,
    val chosenInstrument: CanvasFigure? = null,
    val chosenColor: Color = Color(0xFF1976D2),
    val currentMode: CanvasMode = CanvasMode.PaintMode.Pencil,
    val lineWidth: Float = 50f,
    val animationSpeed: Long = 1000,
    val currentFrame: SnapshotStateList<CanvasFiguresData> = SnapshotStateList(),
    val previousFrame: SnapshotStateList<CanvasFiguresData> = SnapshotStateList(),
    val allFrames: List<SnapshotStateList<CanvasFiguresData>> = listOf(),
    val stackSize: Int = 0
) {
    companion object {
        val EMPTY = CanvasState()
    }
}