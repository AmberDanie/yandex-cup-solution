package com.example.yandex_cup_solution.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.CanvasMode
import com.example.yandex_cup_solution.domain.ViewModelEvent

@Composable
fun DrawableBottomPanel(
    chosenColor: Color,
    currentMode: CanvasMode,
    modifier: Modifier = Modifier,
    onViewModelEvent: (ViewModelEvent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        PencilIcon(
            tintColor = iconTintColor(currentMode, CanvasMode.PaintMode.Pencil),
            modifier = Modifier.clickable {
                if (currentMode !is CanvasMode.Disabled) {
                    onViewModelEvent(ViewModelEvent.BottomPanelEvent.PencilEvent)
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        BrushIcon(
            tintColor = iconTintColor(currentMode, CanvasMode.PaintMode.Brush),
            modifier = Modifier.clickable {
                if (currentMode !is CanvasMode.Disabled) {
                    onViewModelEvent(ViewModelEvent.BottomPanelEvent.BrushEvent)
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        EraseIcon(
            tintColor = iconTintColor(currentMode, CanvasMode.PaintMode.Eraser),
            modifier = Modifier.clickable {
                if (currentMode !is CanvasMode.Disabled) {
                    onViewModelEvent(ViewModelEvent.BottomPanelEvent.EraserEvent)
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        InstrumentsIcon(
            tintColor = iconTintColor(currentMode, CanvasMode.Instruments),
            modifier = Modifier.clickable {
                if (currentMode !is CanvasMode.Disabled) {
                    onViewModelEvent(ViewModelEvent.BottomPanelEvent.InstrumentsEvent.OpenDialog)
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        ColorIcon(
            chosenColor = chosenColor,
            borderColor = colorResource(id = R.color.green),
            isCurrentMode = currentMode == CanvasMode.ColorPicker,
            modifier = Modifier.clickable {
                if (currentMode !is CanvasMode.Disabled) {
                    onViewModelEvent(ViewModelEvent.BottomPanelEvent.ColorPicker.OpenDialog)
                }
            }
        )
    }
}

@Composable
private fun iconTintColor(currentMode: CanvasMode, iconMode: CanvasMode) = colorResource(
    id = when (currentMode) {
        is CanvasMode.Disabled -> R.color.is_disabled
        iconMode -> R.color.green
        else -> R.color.is_active
    }
)

@Composable
fun PencilIcon(
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_pencil_32),
        tint = tintColor,
        contentDescription = stringResource(R.string.choose_pencil),
        modifier = modifier
    )
}

@Composable
fun BrushIcon(
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_brush_32),
        tint = tintColor,
        contentDescription = stringResource(R.string.choose_brush),
        modifier = modifier
    )
}

@Composable
fun EraseIcon(
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_erase_32),
        tint = tintColor,
        contentDescription = stringResource(R.string.choose_eraser),
        modifier = modifier
    )
}

@Composable
fun InstrumentsIcon(
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_instruments_32),
        tint = tintColor,
        contentDescription = stringResource(R.string.choose_instruments),
        modifier = modifier
    )
}

@Composable
fun ColorIcon(
    chosenColor: Color,
    borderColor: Color,
    isCurrentMode: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .padding(start = 16.dp)
    ) {
        if (isCurrentMode) {
            drawCircle(
                color = borderColor,
                radius = 18.dp.toPx()
            )
        }
        drawCircle(color = chosenColor, radius = 16.dp.toPx())
    }
}
