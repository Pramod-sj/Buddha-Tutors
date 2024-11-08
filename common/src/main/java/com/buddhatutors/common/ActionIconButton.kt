package com.buddhatutors.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A composable function to display an icon button with a painter.
 *
 * @param modifier The modifier to be applied to the icon button.
 * @param painter The painter to be displayed as the icon.
 * @param iconTint The tint color of the icon.
 * @param enable Whether the icon button is enabled or not.
 * @param click The callback to be invoked when the icon button is clicked.
 */
@Composable
fun ActionIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    iconTint: Color = Color.White,
    enable: Boolean = true,
    click: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { click() },
        enabled = enable
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = iconTint, // Icon color
        )
    }
}


/**
 * A composable function to display an icon button with an image vector.
 *
 * @param modifier The modifier to be applied to the icon button.
 * @param imageVector The image vector to be displayed as the icon.
 * @param iconTint The tint color of the icon.
 * @param click The callback to be invoked when the icon button is clicked.
 */
@Composable
fun ActionIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconTint: Color = Color.White,
    click: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { click() }) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = iconTint, // Icon color
        )
    }
}