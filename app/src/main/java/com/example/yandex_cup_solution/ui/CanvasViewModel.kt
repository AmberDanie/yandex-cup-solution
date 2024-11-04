package com.example.yandex_cup_solution.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_cup_solution.data.CanvasRepository
import com.example.yandex_cup_solution.domain.CanvasMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack

data class CanvasState(
    val paletteIsVisible: Boolean = false,
    val paletteIsExpanded: Boolean = false,
    val instrumentsIsExpanded: Boolean = false,
    val deleteDialogIsExpanded: Boolean = false,
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

enum class ArrowMove {
    BACK, FORWARD
}

sealed interface FrameInteraction {
    class Delete(val deleteInteraction: DeleteInteraction?) : FrameInteraction
    data object Add : FrameInteraction
    data object Duplicate : FrameInteraction
}

enum class PauseResumeInteraction {
    PAUSE, RESUME
}

enum class DeleteInteraction {
    DELETE_ONE, DELETE_ALL
}

class CanvasViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val canvasRepository: CanvasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CanvasState.EMPTY)
    val uiState = _uiState.asStateFlow()

    private val lastChosenMode: MutableStateFlow<CanvasMode> =
        MutableStateFlow(CanvasMode.PaintMode.Pencil)

    private val removedFrames: Stack<CanvasFiguresData> = Stack()

    init {
        viewModelScope.launch {
            val flow = canvasRepository.getAnimationFrames()
            flow.collect { frames ->
                _uiState.update {
                    it.copy(
                        allFrames = frames,
                        currentFrame = frames.lastOrNull() ?: SnapshotStateList(),
                        previousFrame = if (frames.size > 1) frames[frames.size - 2] else SnapshotStateList()
                    )
                }
            }
        }
    }

    fun expandDeleteDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    deleteDialogIsExpanded = !it.deleteDialogIsExpanded
                )
            }
        }
    }

    fun onDeleteClick(deleteInteraction: DeleteInteraction?) {
        when (deleteInteraction) {
            DeleteInteraction.DELETE_ONE -> deleteFrame()
            DeleteInteraction.DELETE_ALL -> deleteAllFrames()
            null -> {}
        }
        expandDeleteDialog()
    }

    fun sliderValueUpdate(mode: CanvasMode, width: Float) {
        viewModelScope.launch {
            when (mode) {
                is CanvasMode.Disabled -> updateAnimationSpeed(width)
                else -> updateLineWidth(width)
            }
        }
    }

    private fun updateAnimationSpeed(width: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    animationSpeed = ((100 - width) * (1000 / 50)).toLong()
                )
            }
        }
    }

    fun interactPauseAndPlay(pauseResumeInteraction: PauseResumeInteraction) {
        when (pauseResumeInteraction) {
            PauseResumeInteraction.PAUSE -> onPause()
            PauseResumeInteraction.RESUME -> onResume()
        }
    }

    private fun onPause() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentMode = lastChosenMode.value,
                    currentFrame = it.allFrames.lastOrNull() ?: SnapshotStateList()
                )
            }
        }
    }

    private fun onResume() {
        viewModelScope.launch {
            lastChosenMode.update {
                _uiState.value.currentMode
            }
            _uiState.update {
                it.copy(
                    currentMode = CanvasMode.Disabled
                )
            }
            while (_uiState.value.currentMode is CanvasMode.Disabled) {
                _uiState.value.allFrames.forEach { frame ->
                    if (_uiState.value.currentMode !is CanvasMode.Disabled) {
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            currentFrame = frame
                        )
                    }
                    delay(_uiState.value.animationSpeed)
                }
            }
        }
    }

    fun interactFrames(frameInteraction: FrameInteraction) {
        viewModelScope.launch {
            when (frameInteraction) {
                is FrameInteraction.Delete -> onDeleteClick(frameInteraction.deleteInteraction)
                is FrameInteraction.Add -> addFrame()
                is FrameInteraction.Duplicate -> duplicateCurrentFrame()
            }
            cleanStack()
        }
    }

    private fun duplicateCurrentFrame() {
        viewModelScope.launch {
            val currentFrame = _uiState.value.currentFrame.toMutableStateList()
            canvasRepository.addAnimationFrame(currentFrame)
        }
    }

    private fun deleteFrame() {
        viewModelScope.launch {
            canvasRepository.deleteAnimationFrame()
        }
    }

    private fun deleteAllFrames() {
        viewModelScope.launch {
            canvasRepository.deleteAllFrames()
        }
    }

    private fun addFrame() {
        viewModelScope.launch {
            val currentFrame = _uiState.value.currentFrame
            canvasRepository.addAnimationFrame(currentFrame)
        }
    }

    fun cleanStack() {
        viewModelScope.launch {
            removedFrames.clear()
            _uiState.update {
                it.copy(
                    stackSize = 0
                )
            }
        }
    }

    fun onFrameArrowClick(direction: ArrowMove) {
        viewModelScope.launch {
            when (direction) {
                ArrowMove.BACK -> undoPrevious()
                ArrowMove.FORWARD -> returnPrevious()
            }
        }
    }

    private fun undoPrevious() {
        val tempList = _uiState.value.currentFrame
        val lastElement = tempList.removeLast()
        removedFrames.add(lastElement)
        _uiState.update {
            it.copy(
                currentFrame = tempList,
                stackSize = removedFrames.size
            )
        }
    }

    private fun returnPrevious() {
        val tempList = _uiState.value.currentFrame
        tempList.add(removedFrames.removeLast())
        _uiState.update {
            it.copy(
                currentFrame = tempList,
                stackSize = removedFrames.size
            )
        }
    }

    fun onInstrumentsClick(figure: CanvasFigure? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    instrumentsIsExpanded = !it.instrumentsIsExpanded,
                    chosenInstrument = figure
                )
            }
        }
    }

    fun onUpdateFrameFigures(list: SnapshotStateList<CanvasFiguresData>) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentFrame = list
                )
            }
        }
    }

    private fun updateLineWidth(width: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    lineWidth = width
                )
            }
        }
    }

    fun onColorPaletteClick() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    paletteIsVisible = !it.paletteIsVisible,
                    currentMode = if (it.paletteIsVisible) {
                        lastChosenMode.value
                    } else {
                        it.currentMode
                    }
                )
            }
            delay(1000)
            _uiState.update {
                it.copy(
                    paletteIsExpanded = it.paletteIsVisible && it.paletteIsExpanded
                )
            }
        }
    }

    fun onColorPaletteExpand() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    paletteIsExpanded = !it.paletteIsExpanded
                )
            }
        }
    }

    fun chooseColor(color: Color) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    chosenColor = color
                )
            }
            onColorPaletteClick()
        }
    }

    fun updateCurrentMode(newMode: CanvasMode) {
        viewModelScope.launch {
            if (newMode != CanvasMode.ColorPicker) {
                lastChosenMode.update {
                    newMode
                }
            }
            _uiState.update { state ->
                state.copy(
                    currentMode = newMode
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): CanvasViewModel
    }
}
