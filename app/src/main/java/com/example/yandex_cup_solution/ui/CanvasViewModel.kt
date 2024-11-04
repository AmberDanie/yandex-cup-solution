package com.example.yandex_cup_solution.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandex_cup_solution.data.CanvasRepository
import com.example.yandex_cup_solution.domain.CanvasMode
import com.example.yandex_cup_solution.domain.ViewModelEvent
import com.example.yandex_cup_solution.utils.addTriangle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack
import java.util.concurrent.ThreadLocalRandom

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

    fun onEvent(viewModelEvent: ViewModelEvent) {
        viewModelScope.launch {
            when (viewModelEvent) {
                is ViewModelEvent.CleanStack -> cleanStack()
                is ViewModelEvent.SliderEvent -> sliderValueUpdate(
                    viewModelEvent.mode,
                    viewModelEvent.width
                )

                is ViewModelEvent.CanvasEvent -> canvasEvents(viewModelEvent)
                is ViewModelEvent.BottomPanelEvent -> bottomPanelEvents(viewModelEvent)
                is ViewModelEvent.TopPanelEvent -> topPanelEvents(viewModelEvent)
            }
        }
    }

    private suspend fun canvasEvents(viewModelEvent: ViewModelEvent.CanvasEvent) {
        withContext(Dispatchers.IO) {
            when (viewModelEvent) {
                is ViewModelEvent.CanvasEvent.UpdateFrames -> onUpdateFrameFigures(viewModelEvent.frame)
            }
        }
    }

    private suspend fun bottomPanelEvents(viewModelEvent: ViewModelEvent.BottomPanelEvent) {
        withContext(Dispatchers.IO) {
            when (viewModelEvent) {
                is ViewModelEvent.BottomPanelEvent.PencilEvent -> updateCurrentMode(CanvasMode.PaintMode.Pencil)
                is ViewModelEvent.BottomPanelEvent.BrushEvent -> updateCurrentMode(CanvasMode.PaintMode.Brush)
                is ViewModelEvent.BottomPanelEvent.EraserEvent -> updateCurrentMode(CanvasMode.PaintMode.Eraser)
                is ViewModelEvent.BottomPanelEvent.InstrumentsEvent -> instrumentsEvents(
                    viewModelEvent
                )

                is ViewModelEvent.BottomPanelEvent.ColorPicker -> colorPickerEvents(viewModelEvent)
            }
        }
    }

    private suspend fun topPanelEvents(viewModelEvent: ViewModelEvent.TopPanelEvent) {
        withContext(Dispatchers.IO) {
            when (viewModelEvent) {
                is ViewModelEvent.TopPanelEvent.GenerateFrames -> generateEvents(viewModelEvent)
                is ViewModelEvent.TopPanelEvent.BackArrow -> undoPrevious()
                is ViewModelEvent.TopPanelEvent.ForwardArrow -> returnPrevious()
                is ViewModelEvent.TopPanelEvent.DeleteFrameEvent -> deleteEvents(viewModelEvent)
                is ViewModelEvent.TopPanelEvent.AddFrame -> addFrame()
                is ViewModelEvent.TopPanelEvent.DuplicateFrame -> duplicateCurrentFrame()
                ViewModelEvent.TopPanelEvent.Pause -> onPause()
                ViewModelEvent.TopPanelEvent.Resume -> onResume()
            }
        }
    }

    private suspend fun generateEvents(viewModelEvent: ViewModelEvent.TopPanelEvent.GenerateFrames) {
        when (viewModelEvent) {
            is ViewModelEvent.TopPanelEvent.GenerateFrames.OpenDialog -> openGenerateDialog()
            is ViewModelEvent.TopPanelEvent.GenerateFrames.AddRandomFrames -> generateFrames(
                viewModelEvent.count
            )
        }
    }

    private suspend fun deleteEvents(viewModelEvent: ViewModelEvent.TopPanelEvent.DeleteFrameEvent) {
        withContext(Dispatchers.IO) {
            when (viewModelEvent) {
                is ViewModelEvent.TopPanelEvent.DeleteFrameEvent.DeleteAll -> deleteAllFrames()
                is ViewModelEvent.TopPanelEvent.DeleteFrameEvent.DeleteOne -> deleteFrame()
                is ViewModelEvent.TopPanelEvent.DeleteFrameEvent.OpenDialog -> expandDeleteDialog()
            }
        }
    }

    private suspend fun instrumentsEvents(viewModelEvent: ViewModelEvent.BottomPanelEvent.InstrumentsEvent) {
        withContext(Dispatchers.IO) {
            updateCurrentMode(CanvasMode.Instruments)

            when (viewModelEvent) {
                is ViewModelEvent.BottomPanelEvent.InstrumentsEvent.OpenDialog -> onInstrumentsClick()
                is ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseSquare -> onInstrumentsClick(
                    CanvasFigure.Square
                )

                is ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseCircle -> onInstrumentsClick(
                    CanvasFigure.Circle
                )

                is ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseTriangle -> onInstrumentsClick(
                    CanvasFigure.Triangle
                )
            }
        }
    }

    private suspend fun colorPickerEvents(viewModelEvent: ViewModelEvent.BottomPanelEvent.ColorPicker) {
        withContext(Dispatchers.IO) {
            when (viewModelEvent) {
                is ViewModelEvent.BottomPanelEvent.ColorPicker.OpenDialog -> onColorPaletteClick()
                is ViewModelEvent.BottomPanelEvent.ColorPicker.ExpandDialog -> onColorPaletteExpand()
                is ViewModelEvent.BottomPanelEvent.ColorPicker.ChooseColor -> chooseColor(
                    viewModelEvent.color
                )
            }
        }
    }

    private suspend fun openGenerateDialog() {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    generateFramesDialogIsExpanded = !it.generateFramesDialogIsExpanded
                )
            }
        }
    }

    private suspend fun generateFrames(count: Int) {
        withContext(Dispatchers.IO) {
            openGenerateDialog()
            lastChosenMode.value = _uiState.value.currentMode
            _uiState.update {
                it.copy(
                    currentMode = CanvasMode.Disabled
                )
            }
            repeat(count) {
                generateFrame()
            }
            updateCurrentMode(lastChosenMode.value)
        }
    }

    private suspend fun generateFrame() {
        val rand = ThreadLocalRandom.current()
        val x = rand.nextDouble(0.0, 1500.0)
        val y = rand.nextDouble(0.0, 2000.0)
        val frame = SnapshotStateList<CanvasFiguresData>()
        frame.addTriangle(
            offset = Offset(x.toFloat(), y.toFloat()),
            color = _uiState.value.chosenColor,
            lineWidth = rand.nextDouble(3.0, 100.0).toFloat(),
            alpha = 1f
        )
        canvasRepository.addAnimationFrame(frame)
    }

    private suspend fun expandDeleteDialog() {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    deleteDialogIsExpanded = !it.deleteDialogIsExpanded
                )
            }
        }
    }

    private suspend fun sliderValueUpdate(mode: CanvasMode, width: Float) {
        withContext(Dispatchers.IO) {
            when (mode) {
                is CanvasMode.Disabled -> updateAnimationSpeed(width)
                else -> updateLineWidth(width)
            }
        }
    }

    private suspend fun updateAnimationSpeed(width: Float) {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    animationSpeed = ((100 - width) * (1000 / 50)).toLong()
                )
            }
        }
    }

    private suspend fun onPause() {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    currentMode = lastChosenMode.value,
                    currentFrame = it.allFrames.lastOrNull() ?: SnapshotStateList()
                )
            }
        }
    }

    private suspend fun onResume() {
        withContext(Dispatchers.IO) {
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
                        return@withContext
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

    private suspend fun duplicateCurrentFrame() {
        withContext(Dispatchers.IO) {
            val currentFrame = _uiState.value.currentFrame.toMutableStateList()
            canvasRepository.addAnimationFrame(currentFrame)
        }
    }

    private suspend fun deleteFrame() {
        withContext(Dispatchers.IO) {
            canvasRepository.deleteAnimationFrame()
            expandDeleteDialog()
        }
    }

    private suspend fun deleteAllFrames() {
        withContext(Dispatchers.IO) {
            canvasRepository.deleteAllFrames()
            expandDeleteDialog()
        }
    }

    private suspend fun addFrame() {
        withContext(Dispatchers.IO) {
            val currentFrame = _uiState.value.currentFrame
            canvasRepository.addAnimationFrame(currentFrame)
        }
    }

    private suspend fun cleanStack() {
        withContext(Dispatchers.IO) {
            removedFrames.clear()
            _uiState.update {
                it.copy(
                    stackSize = 0
                )
            }
        }
    }

    private suspend fun undoPrevious() {
        withContext(Dispatchers.IO) {
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
    }

    private suspend fun returnPrevious() {
        withContext(Dispatchers.IO) {
            val tempList = _uiState.value.currentFrame
            tempList.add(removedFrames.removeLast())
            _uiState.update {
                it.copy(
                    currentFrame = tempList,
                    stackSize = removedFrames.size
                )
            }
        }
    }

    private suspend fun onInstrumentsClick(figure: CanvasFigure? = null) {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    instrumentsIsExpanded = !it.instrumentsIsExpanded,
                    chosenInstrument = figure
                )
            }
        }
    }

    private suspend fun onUpdateFrameFigures(list: SnapshotStateList<CanvasFiguresData>) {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    currentFrame = list
                )
            }
        }
    }

    private suspend fun updateLineWidth(width: Float) {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    lineWidth = width
                )
            }
        }
    }

    private suspend fun onColorPaletteClick() {
        withContext(Dispatchers.IO) {
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

    private suspend fun onColorPaletteExpand() {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    paletteIsExpanded = !it.paletteIsExpanded
                )
            }
        }
    }

    private suspend fun chooseColor(color: Color) {
        withContext(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    chosenColor = color
                )
            }
            onColorPaletteClick()
        }
    }

    private suspend fun updateCurrentMode(newMode: CanvasMode) {
        withContext(Dispatchers.IO) {
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
