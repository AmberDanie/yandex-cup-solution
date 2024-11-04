package com.example.yandex_cup_solution.domain

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.example.yandex_cup_solution.ui.CanvasFiguresData

sealed interface ViewModelEvent {

    sealed interface TopPanelEvent : ViewModelEvent {
        data object BackArrow : TopPanelEvent
        data object ForwardArrow : TopPanelEvent

        sealed interface DeleteFrameEvent : TopPanelEvent {
            data object OpenDialog : DeleteFrameEvent
            data object DeleteOne : DeleteFrameEvent
            data object DeleteAll : DeleteFrameEvent
        }

        data object AddFrame : TopPanelEvent
        data object DuplicateFrame : TopPanelEvent
        data object Pause : TopPanelEvent
        data object Resume : TopPanelEvent
    }

    sealed interface BottomPanelEvent : ViewModelEvent {
        data object PencilEvent : BottomPanelEvent
        data object BrushEvent : BottomPanelEvent
        data object EraserEvent : BottomPanelEvent

        sealed interface InstrumentsEvent : BottomPanelEvent {
            data object OpenDialog : InstrumentsEvent
            data object ChooseSquare : InstrumentsEvent
            data object ChooseCircle : InstrumentsEvent
            data object ChooseTriangle : InstrumentsEvent
        }

        sealed interface ColorPicker : BottomPanelEvent {
            data object OpenDialog : ColorPicker
            data object ExpandDialog : ColorPicker
            class ChooseColor(val color: Color) : ColorPicker
        }
    }

    sealed interface CanvasEvent : ViewModelEvent {
        class UpdateFrames(val frame: SnapshotStateList<CanvasFiguresData>) : CanvasEvent
    }

    class SliderEvent(val width: Float, val mode: CanvasMode) : ViewModelEvent

    data object CleanStack : ViewModelEvent
}


