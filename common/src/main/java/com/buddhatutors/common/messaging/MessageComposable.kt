package com.buddhatutors.common.messaging

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buddhatutors.common.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Preview
@Composable
fun PreviewMessageComposable() {
    MessageComposable(
        Message.Success("This is done! This is done! This is done! This is done!"),
        onDismiss = {})
}


@Composable
fun MessageComposable(
    message: Message,
    onDismiss: () -> Unit
) {

    val localView = LocalView.current

    val isVisible = remember { MutableTransitionState(true) }

    // Trigger visibility change only when the message object changes
    LaunchedEffect(key1 = message) {
        isVisible.targetState = true
        localView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        delay(3000) // Wait for 2 seconds
        isVisible.targetState = false
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            Pair(isVisible.currentState, isVisible.targetState)
        }.collectLatest { (curr, target) ->
            if (!curr && !target) {
                onDismiss.invoke()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {

        AnimatedVisibility(
            visibleState = isVisible,
            enter = scaleIn(
                initialScale = 0.8f, // Start at 50% size
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(),
            exit = scaleOut(
                targetScale = 0.8f, // Shrink back to 50% size
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeOut(),
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = CardDefaults.elevatedShape,
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ) {

                Row(
                    modifier = Modifier
                        .then(
                            when (message) {
                                is Message.Success -> Modifier.background(Color(0xFF32A72C))
                                is Message.Warning -> Modifier.background(Color(0xFFDF6817))
                            }
                        )
                        .padding(start = 4.dp)
                        .background(MaterialTheme.colorScheme.background, CardDefaults.shape)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(Modifier.width(12.dp))

                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = when (message) {
                            is Message.Success -> painterResource(R.drawable.ic_toast_success)
                            is Message.Warning -> painterResource(R.drawable.ic_toast_warning)
                        },
                        contentDescription = ""
                    )

                    Spacer(Modifier.width(16.dp))

                    Text(
                        modifier = Modifier.weight(1f),
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (message is Message.Warning
                        && message.actionLabel != null
                        && message.actionCallback != null
                    ) {
                        TextButton(
                            onClick = message.actionCallback
                        ) {
                            Text(text = message.actionLabel)
                        }
                    } else {
                        Spacer(Modifier.width(12.dp))
                    }


                }

            }

        }
    }


}
