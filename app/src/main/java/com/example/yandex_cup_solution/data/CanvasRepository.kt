package com.example.yandex_cup_solution.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.yandex_cup_solution.ui.CanvasFiguresData
import kotlinx.coroutines.flow.Flow

interface CanvasRepository {
    fun getAnimationFrames(): Flow<List<SnapshotStateList<CanvasFiguresData>>>
    suspend fun deleteAnimationFrame()
    suspend fun deleteAllFrames()
    suspend fun addAnimationFrame(frame: SnapshotStateList<CanvasFiguresData>)
}
