package com.example.yandex_cup_solution.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.ViewModelEvent
import com.example.yandex_cup_solution.ui.theme.CanvasColors

@Composable
fun ColorPickerDialog(
    isVisible: Boolean,
    isExpanded: Boolean,
    onViewModelEvent: (ViewModelEvent.BottomPanelEvent.ColorPicker) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Dialog(
            onDismissRequest = { onViewModelEvent(ViewModelEvent.BottomPanelEvent.ColorPicker.OpenDialog) },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 10.dp)
                            .background(Color.Transparent)
                    ) {
                        if (isExpanded) {
                            ExpandedColorsLayout(

                            ) { color ->
                                onViewModelEvent(
                                    ViewModelEvent.BottomPanelEvent.ColorPicker.ChooseColor(
                                        color
                                    )
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .padding(bottom = 100.dp, end = 10.dp)
                            .background(Color.Transparent)
                    ) {
                        ColorSelectionLayout(
                            tintColor = colorResource(id = if (isExpanded) R.color.green else R.color.is_active),
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .clickable { onViewModelEvent(ViewModelEvent.BottomPanelEvent.ColorPicker.ExpandDialog) },
                            onViewModelEvent = onViewModelEvent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectionLayout(
    onViewModelEvent: (ViewModelEvent.BottomPanelEvent.ColorPicker) -> Unit,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        colorResource(id = R.color.white),
        colorResource(id = R.color.red),
        colorResource(id = R.color.black),
        colorResource(id = R.color.blue)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_color_palette_32),
            contentDescription = stringResource(R.string.open_all_colors),
            tint = tintColor,
            modifier = modifier
        )

        colors.forEach { color ->
            Canvas(
                modifier = Modifier
                    .padding(end = 48.dp)
                    .clickable {
                        onViewModelEvent(
                            ViewModelEvent.BottomPanelEvent.ColorPicker.ChooseColor(
                                color
                            )
                        )
                    }
            ) {
                drawCircle(color = color, radius = 16.dp.toPx())
            }
        }
    }
}

@Composable
fun ExpandedColorsLayout(
    chooseColor: (Color) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 42.dp)
    ) {
        ColorRow(colorList = CanvasColors.dullColors) { color -> chooseColor(color) }
        ColorRow(colorList = CanvasColors.lightColors) { color -> chooseColor(color) }
        ColorRow(colorList = CanvasColors.originalColors) { color -> chooseColor(color) }
        ColorRow(colorList = CanvasColors.darkColors) { color -> chooseColor(color) }
        ColorRow(colorList = CanvasColors.veryDarkColors) { color -> chooseColor(color) }
    }
}

@Composable
fun ColorRow(
    colorList: List<Color>,
    chooseColor: (Color) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .padding(start = 6.dp)
    ) {
        colorList.forEachIndexed { index, color ->
            Canvas(
                modifier = Modifier.clickable {
                    chooseColor(color)
                }
            ) {
                drawCircle(color = color, radius = 16.dp.toPx())
            }
        }
    }
}