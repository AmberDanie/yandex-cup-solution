package com.example.yandex_cup_solution.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.yandex_cup_solution.ui.CanvasFiguresData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CanvasRepositoryImpl @Inject constructor() : CanvasRepository {

    private val _animationFrames = MutableStateFlow(listOf<SnapshotStateList<CanvasFiguresData>>(
        SnapshotStateList()
    ))

    override suspend fun addAnimationFrame(frame: SnapshotStateList<CanvasFiguresData>) {
        withContext(Dispatchers.IO) {
            val tempList = _animationFrames.value.toMutableList()
            if (tempList.isNotEmpty()) {
                tempList.removeLast()
            }
            val isDuplicate = frame == (_animationFrames.value.lastOrNull() ?: false)
            tempList.add(frame)
            if (!isDuplicate) {
                val newList = SnapshotStateList<CanvasFiguresData>()
                frame.forEach { line ->
                    newList.add(line)
                }
                tempList.add(newList)
            } else {
                tempList.add(SnapshotStateList())
            }
            _animationFrames.update {
                tempList
            }
        }
    }

    override suspend fun deleteAnimationFrame() {
        withContext(Dispatchers.IO) {
            val tempList = _animationFrames.value.toMutableList()
            if (tempList.isNotEmpty()) {
                tempList.removeLast()
                if (tempList.isEmpty()) {
                    tempList.add(SnapshotStateList())
                }
            }
            _animationFrames.update {
                tempList
            }
        }
    }

    override fun getAnimationFrames(): Flow<List<SnapshotStateList<CanvasFiguresData>>> {
        return _animationFrames
    }
}
