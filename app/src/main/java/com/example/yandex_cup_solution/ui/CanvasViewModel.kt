package com.example.yandex_cup_solution.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
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
    val chosenInstrument: CanvasFigure? = null,
    val chosenColor: Color = Color(0xFF1976D2),
    val currentMode: CanvasMode = CanvasMode.PaintMode.Pencil,
    val lineWidth: Float = 50f,
    val currentFrame: SnapshotStateList<CanvasFiguresData> = SnapshotStateList(),
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

enum class FrameInteractor {
    DELETE, ADD
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
                        currentFrame = frames.lastOrNull() ?: SnapshotStateList()
                    )
                }
            }
        }
    }

    fun interactFrames(frameInteraction: FrameInteractor) {
        when (frameInteraction) {
            FrameInteractor.DELETE -> deleteFrame()
            FrameInteractor.ADD -> addFrame()
        }
    }

    private fun deleteFrame() {
        viewModelScope.launch {
            canvasRepository.deleteAnimationFrame()
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
        when (direction) {
            ArrowMove.BACK -> undoPrevious()
            ArrowMove.FORWARD -> returnPrevious()
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

    fun updateLineWidth(width: Float) {
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
