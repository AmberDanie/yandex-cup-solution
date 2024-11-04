package com.example.yandex_cup_solution.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yandex_cup_solution.R
import com.example.yandex_cup_solution.domain.CanvasMode

@Composable
fun DrawableTopPanel(
    modifier: Modifier = Modifier,
    currentMode: CanvasMode,
    onFrameInteract: (FrameInteraction) -> Unit,
    onFrameArrowClick: (ArrowMove) -> Unit,
    onPauseResumeClick: (PauseResumeInteraction) -> Unit,
    forwardIsActive: Boolean,
    backIsActive: Boolean
) {
    val isNotResuming = currentMode !is CanvasMode.Disabled

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackArrow(
                isActive = backIsActive,
                modifier = Modifier.clickable {
                    if (backIsActive && isNotResuming) {
                        onFrameArrowClick(ArrowMove.BACK)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ForwardArrow(
                isActive = forwardIsActive,
                modifier = Modifier.clickable {
                    if (forwardIsActive && isNotResuming) {
                        onFrameArrowClick(ArrowMove.FORWARD)
                    }
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeleteFrameIcon(
                isActive = isNotResuming,
                modifier = Modifier.clickable {
                    if (isNotResuming) {
                        onFrameInteract(FrameInteraction.DELETE)
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            CreateFrameIcon(
                isActive = isNotResuming,
                modifier = Modifier.clickable {
                    if (isNotResuming) {
                        onFrameInteract(FrameInteraction.ADD)
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            DuplicateFrameIcon(
                isActive = isNotResuming,
                modifier = Modifier.clickable {
                    if (isNotResuming) {
                        onFrameInteract(FrameInteraction.DUPLICATE)
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            OpenFramesIcon(
                isActive = false
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            PauseIcon(
                isActive = !isNotResuming,
                modifier = Modifier.clickable {
                    if (!isNotResuming) {
                        onPauseResumeClick(PauseResumeInteraction.PAUSE)
                    }
                })
            Spacer(modifier = Modifier.width(16.dp))
            ResumeIcon(
                isActive = isNotResuming,
                modifier = Modifier.clickable {
                    if (isNotResuming) {
                        onPauseResumeClick(PauseResumeInteraction.RESUME)
                    }
                }
            )
        }
    }
}

@Composable
fun BackArrow(
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorValue = if (isActive) R.color.is_active else R.color.is_disabled
    Icon(
        painter = painterResource(R.drawable.ic_right_active_24),
        tint = colorResource(id = colorValue),
        contentDescription = stringResource(R.string.undo_previous_action),
        modifier = modifier.size(32.dp)
    )
}

@Composable
fun ForwardArrow(
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorValue = if (isActive) R.color.is_active else R.color.is_disabled
    Icon(
        painter = painterResource(id = R.drawable.ic_left_active_24),
        tint = colorResource(id = colorValue),
        contentDescription = stringResource(id = R.string.return_cancelled_action),
        modifier = modifier.size(32.dp)
    )
}

@Composable
fun DeleteFrameIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_bin_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(id = R.string.delete_current_frame),
        modifier = modifier
    )
}

@Composable
fun CreateFrameIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_file_plus_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(id = R.string.create_new_frame),
        modifier = modifier
    )
}

@Composable
fun DuplicateFrameIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.baseline_content_copy_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(R.string.duplicate_current_frame),
        modifier = modifier.size(28.dp)
    )
}

@Composable
fun OpenFramesIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_layers_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(id = R.string.open_all_frames),
        modifier = modifier
    )
}

@Composable
fun PauseIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_pause_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(R.string.pause_the_animation),
        modifier = modifier
    )
}

@Composable
fun ResumeIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_resume_32),
        tint = colorResource(id = if (isActive) R.color.is_active else R.color.is_disabled),
        contentDescription = stringResource(R.string.resume_the_animation),
        modifier = modifier
    )
}