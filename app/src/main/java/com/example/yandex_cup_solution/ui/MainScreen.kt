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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.CanvasMode
import com.example.yandex_cup_solution.domain.ViewModelEvent

@Composable
fun MainScreen(
    uiState: CanvasState,
    onViewModelEvent: (ViewModelEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMode = uiState.currentMode

    Box {
        Column {
            DrawableTopPanel(
                forwardIsActive = uiState.stackSize > 0 && currentMode !is CanvasMode.Disabled,
                currentMode = currentMode,
                backIsActive = uiState.currentFrame.isNotEmpty() && currentMode !is CanvasMode.Disabled,
                modifier = modifier.weight(0.2f),
                onViewModelEvent = onViewModelEvent,
            )
            Box(
                modifier = modifier.weight(1f)
            ) {
                if (currentMode is CanvasMode.Disabled) {
                    ViewingCanvas(
                        frame = uiState.currentFrame,
                        modifier = modifier.fillMaxHeight()
                    )
                } else {
                    DrawableCanvas(
                        currentMode = currentMode,
                        chosenColor = uiState.chosenColor,
                        chosenFigure = uiState.chosenInstrument,
                        lineWidth = uiState.lineWidth,
                        previousFrame = uiState.previousFrame,
                        currentFrame = uiState.currentFrame,
                        modifier = modifier.fillMaxHeight(),
                        onViewModelEvent = onViewModelEvent,
                    )
                }
            }
            DrawableBottomPanel(
                chosenColor = uiState.chosenColor,
                currentMode = uiState.currentMode,
                modifier = modifier.weight(0.2f),
                onViewModelEvent = onViewModelEvent,
            )
        }
        if (currentMode !is CanvasMode.Disabled) {
            Text(
                text = uiState.allFrames.size.toString(),
                fontSize = 40.sp,
                color = colorResource(id = R.color.gray),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 150.dp)
            )
        }

        DeleteDialog(
            onFrameInteract = onViewModelEvent,
            isVisible = uiState.deleteDialogIsExpanded)

        BottomPanelSlider(uiState, onViewModelEvent)

        InstrumentsDialog(
            isVisible = uiState.instrumentsIsExpanded,
            onViewModelEvent = onViewModelEvent,
        )

        ColorPickerDialog(
            isVisible = uiState.paletteIsVisible,
            isExpanded = uiState.paletteIsExpanded,
            onViewModelEvent = onViewModelEvent,
        )
    }
}

@Composable
fun BoxScope.BottomPanelSlider(
    uiState: CanvasState,
    onViewModelEvent: (ViewModelEvent) -> Unit
) {
    Slider(
        value = if (uiState.currentMode is CanvasMode.Disabled) {
            (103f - uiState.animationSpeed.toFloat() * (5f / 100f))
        } else {
            uiState.lineWidth
        },
        onValueChange = { value ->
            val a = value
            onViewModelEvent(ViewModelEvent.SliderEvent(value, uiState.currentMode))
        },
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
}

@Composable
fun DeleteDialog(
    onFrameInteract: (ViewModelEvent.TopPanelEvent.DeleteFrameEvent) -> Unit,
    isVisible: Boolean,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Dialog(
            onDismissRequest = { onFrameInteract(ViewModelEvent.TopPanelEvent.DeleteFrameEvent.OpenDialog) },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(bottom = 100.dp)
                        .background(Color.Transparent)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 32.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete_current_frame),
                            color = colorResource(id = R.color.is_active),
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    onFrameInteract(ViewModelEvent.TopPanelEvent.DeleteFrameEvent.DeleteOne)
                                }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.delete_all_frames),
                            color = colorResource(id = R.color.red),
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    onFrameInteract(ViewModelEvent.TopPanelEvent.DeleteFrameEvent.DeleteAll)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstrumentsDialog(
    isVisible: Boolean,
    onViewModelEvent: (ViewModelEvent) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Dialog(
            onDismissRequest = { onViewModelEvent(ViewModelEvent.BottomPanelEvent.InstrumentsEvent.OpenDialog) },
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
                    InstrumentsLayout(onViewModelEvent = onViewModelEvent)
                }
            }
        }
    }
}

@Composable
fun InstrumentsLayout(
    modifier: Modifier = Modifier,
    onViewModelEvent: (ViewModelEvent.BottomPanelEvent.InstrumentsEvent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(200.dp)
            .padding(16.dp)
    ) {
        Icon(painter = painterResource(
            id = R.drawable.ic_square_24
        ),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.square),
            modifier = Modifier.clickable {
                onViewModelEvent(ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseSquare)
            }
        )
        Icon(painter = painterResource(
            id = R.drawable.ic_circle_24
        ),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.circle),
            modifier = Modifier.clickable {
                onViewModelEvent(ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseCircle)
            }
        )
        Icon(painter = painterResource(
            id = R.drawable.ic_triangle_24
        ),
            tint = colorResource(id = R.color.is_active),
            contentDescription = stringResource(R.string.triangle),
            modifier = Modifier.clickable {
                onViewModelEvent(ViewModelEvent.BottomPanelEvent.InstrumentsEvent.ChooseTriangle)
            }
        )
        Icon(
            painter = painterResource(
                id = R.drawable.ic_arrow_up_24
            ),
            tint = colorResource(id = R.color.is_disabled),
            contentDescription = stringResource(R.string.arrow_up)
        )
    }
}
