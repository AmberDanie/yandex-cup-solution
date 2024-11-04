package com.example.yandex_cup_solution.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.CanvasMode

@Composable
fun MainScreen(
    uiState: CanvasState,
    cleanStack: () -> Unit,
    onFrameInteract: (FrameInteractor) -> Unit,
    onFrameArrowClick: (ArrowMove) -> Unit,
    onInstrumentsClick: (CanvasFigure?) -> Unit,
    onUpdateFrameFigures: (SnapshotStateList<CanvasFiguresData>) -> Unit,
    updateLineWidth: (Float) -> Unit,
    onModeClick: (CanvasMode) -> Unit,
    onColorPaletteExpand: () -> Unit,
    chooseColor: (Color) -> Unit,
    onColorPaletteClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val currentMode = uiState.currentMode

    Box {
        Column {
            DrawableTopPanel(
                forwardIsActive = uiState.stackSize > 0,
                backIsActive = uiState.currentFrame.isNotEmpty(),
                onFrameArrowClick = onFrameArrowClick,
                onFrameInteract = onFrameInteract,
                modifier = modifier.weight(0.2f)
            )
            DrawableCanvas(
                currentMode = currentMode,
                chosenColor = uiState.chosenColor,
                chosenFigure = uiState.chosenInstrument,
                lineWidth = uiState.lineWidth,
                cleanStack = cleanStack,
                onUpdateFrameFigures = onUpdateFrameFigures,
                currentFrame = uiState.currentFrame,
                modifier = modifier.weight(1f)
            )
            DrawableBottomPanel(
                onColorPaletteClick = onColorPaletteClick,
                onInstrumentsClick = onInstrumentsClick,
                onModeClick = onModeClick,
                chosenColor = uiState.chosenColor,
                currentMode = uiState.currentMode,
                modifier = modifier.weight(0.2f)
            )
        }
        Slider(
            value = uiState.lineWidth,
            onValueChange = { width -> updateLineWidth(width) },
            valueRange = 5f..100f,
            colors = SliderDefaults.colors(
                thumbColor = colorResource(id = R.color.white),
                activeTrackColor = uiState.chosenColor
            ),
            enabled = uiState.currentMode !is CanvasMode.ColorPicker,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 20.dp)
                .padding(horizontal = 100.dp)
        )
        InstrumentsDialog(
            isVisible = uiState.instrumentsIsExpanded,
            onInstrumentsClick = onInstrumentsClick
        )
        ColorPickerDialog(
            onDismiss = onColorPaletteClick,
            onExpand = onColorPaletteExpand,
            chooseColor = chooseColor,
            isVisible = uiState.paletteIsVisible,
            isExpanded = uiState.paletteIsExpanded
        )
    }
}

@Composable
fun InstrumentsDialog(
    isVisible: Boolean,
    onInstrumentsClick: (CanvasFigure?) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Dialog(
            onDismissRequest = { onInstrumentsClick(null) },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .padding(bottom = 100.dp, end = 10.dp)
                        .background(Color.Transparent)
                ) {
                    InstrumentsLayout(onInstrumentsClick = onInstrumentsClick)
                }
            }
        }
    }
}

@Composable
fun InstrumentsLayout(
    modifier: Modifier = Modifier,
    onInstrumentsClick: (CanvasFigure?) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(200.dp).padding(16.dp)
    ) {
        Icon(painter = painterResource(
            id = R.drawable.ic_square_24),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.square),
            modifier = Modifier.clickable {
                onInstrumentsClick(CanvasFigure.Square)
            }
        )
        Icon(painter = painterResource(
            id = R.drawable.ic_circle_24),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.circle),
            modifier = Modifier.clickable {
                onInstrumentsClick(CanvasFigure.Circle)
            }
        )
        Icon(painter = painterResource(
            id = R.drawable.ic_triangle_24),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.triangle),
            modifier = Modifier.clickable {
                onInstrumentsClick(CanvasFigure.Triangle)
            }
        )
        Icon(painter = painterResource(
            id = R.drawable.ic_arrow_up_24),
            tint = colorResource(id = R.color.is_disabled),
            contentDescription = stringResource(R.string.arrow_up)
        )
    }
}
