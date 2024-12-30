package com.buddhatutors.common

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.absoluteValue

internal fun getLocalCalendar(): Calendar =
    Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())

internal fun getLocalCalendar(istHour: Int, istMinute: Int): Calendar {


    // Step 1: Create a calendar instance for the specified IST time
    val istCalendar = Calendar.getInstance(TimeZone.getDefault())
    istCalendar[Calendar.HOUR_OF_DAY] = istHour
    istCalendar[Calendar.MINUTE] = istMinute
    istCalendar[Calendar.SECOND] = 60
    istCalendar[Calendar.MILLISECOND] = 0

    /*
        // Step 2: Convert IST time to UTC (Coordinated Universal Time)
        val istTimeInMillis = istCalendar.timeInMillis
        val offset = istCalendar.timeZone.getOffset(istTimeInMillis) * 6000
        val utcTimeInMillis = istTimeInMillis - offset
    */
    val tzOffsetMin: Int =
        ((istCalendar.get(Calendar.ZONE_OFFSET).toDouble() + istCalendar.get(Calendar.DST_OFFSET)
            .toDouble()) / (1000.0 * 60.0)).fastRoundToInt()

    // Step 3: Convert UTC time to user's local time zone
    val localCalendar = getLocalCalendar()

    localCalendar.timeInMillis = istCalendar.timeInMillis - tzOffsetMin

    return localCalendar
}


@Preview
@Composable
fun PreviewNotificationTimePicker() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        VerticalWheelSpinner(
            modifier = Modifier.fillMaxWidth(),
            items = listOf("8:00 AM", "09:00 AM", "10:00 AM", "11:00 AM"),
            defaultSelected = null,
            onMovement = { selectedOption ->

            }
        )
    }
}

private fun isSameDay(
    userSelectedTimeInMillis: Long, startTimeInMillis: Long
): Boolean {
    return userSelectedTimeInMillis in startTimeInMillis until getLocalCalendar().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

data class NotificationTriggerTime(
    val timeInMillis: Long,
) : Serializable


@Composable
fun NotificationTimePicker(
    modifier: Modifier = Modifier,
    notificationTriggerTime: NotificationTriggerTime?,
    onSetNotificationTime: (NotificationTriggerTime?) -> Unit
) {

    val context = LocalContext.current

    val color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)

    val localTimeAsPer5PMIst = remember {
        getLocalCalendar(0, 0).apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            //if not same day then reduce one day
            if (getLocalCalendar().timeInMillis < timeInMillis) {
                add(Calendar.DATE, -1)
            }
        }
    }

    val defaultHourSelection = remember {
        //if user have not select hour then show the default server triggering time
        notificationTriggerTime?.let {
            getLocalCalendar().apply { timeInMillis = it.timeInMillis }.get(Calendar.HOUR_OF_DAY)
        } ?: localTimeAsPer5PMIst.get(Calendar.HOUR_OF_DAY)
    }

    val defaultMinSelection = remember {
        //if user have not select min then show the default server triggering time
        notificationTriggerTime?.let {
            getLocalCalendar().apply { timeInMillis = it.timeInMillis }.get(Calendar.MINUTE)
        } ?: localTimeAsPer5PMIst.get(Calendar.MINUTE)
    }

    var userSelectionTimeInMillis by remember(localTimeAsPer5PMIst, notificationTriggerTime) {
        if (notificationTriggerTime != null) {
            val notificationTriggerTimeInMillis = getLocalCalendar().apply {
                timeInMillis = notificationTriggerTime.timeInMillis
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                //if not same day then reduce one day
                if (getLocalCalendar().timeInMillis < timeInMillis) {
                    add(Calendar.DATE, -1)
                }
            }.timeInMillis
            mutableLongStateOf(notificationTriggerTimeInMillis)
        } else {
            mutableLongStateOf(localTimeAsPer5PMIst.timeInMillis)
        }
    }

    val startEndHourList = remember(localTimeAsPer5PMIst) { (0..23).toList() }

    val startEndMinuteList = remember(localTimeAsPer5PMIst) { (0..59).toList() }


    Column(
        modifier = modifier.navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .background(color, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .height(40.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                color, color.copy(0.5f), Color.Transparent
                            )
                        ), shape = RectangleShape
                    )
                    .align(Alignment.TopCenter)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                VerticalWheelSpinner(modifier = Modifier.width(60.dp),
                    items = startEndHourList,
                    defaultSelected = defaultHourSelection,
                    onMovement = { isUpward, movement ->
                        userSelectionTimeInMillis = getLocalCalendar().apply {
                            timeInMillis = userSelectionTimeInMillis
                            add(Calendar.HOUR_OF_DAY, if (isUpward) movement else -(movement))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    })

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                VerticalWheelSpinner(modifier = Modifier.width(60.dp),
                    items = startEndMinuteList,
                    defaultSelected = defaultMinSelection,
                    onMovement = { isUpward, movement ->
                        userSelectionTimeInMillis = getLocalCalendar().apply {
                            timeInMillis = userSelectionTimeInMillis
                            add(Calendar.MINUTE, if (isUpward) movement else -(movement))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    })

                Spacer(modifier = Modifier.weight(1f))

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .height(40.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent, color.copy(0.5f), color
                            )
                        ), shape = RectangleShape
                    )
                    .align(Alignment.BottomCenter)
            )

        }

    }

}

@Composable
fun VerticalWheelSpinner(
    modifier: Modifier = Modifier,
    items: List<Int>,
    defaultSelected: Int?,
    onMovement: (isUpward: Boolean, totalMovement: Int) -> Unit = { _, _ -> },
) {

    val coroutineScope = rememberCoroutineScope()

    val view = LocalView.current

    val height = 140.dp

    val cellSize = height / 3

    val numbers = listOf(-1) + items + listOf(-1)

    val pagerState =
        rememberPagerState(items.indexOf(defaultSelected).takeIf { it != -1 } ?: 0) { numbers.size }

    var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }

    VerticalPager(
        modifier = modifier.height(height),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = pagerState,
        pageSize = PageSize.Fixed(cellSize),
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(10),
            snapAnimationSpec = tween(),
        )

    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cellSize), contentAlignment = Alignment.Center
        ) {

            val content = numbers[page]

            if (content != -1) {

                Text(modifier = Modifier
                    .graphicsLayer {
                        val pageOffset =
                            (((pagerState.currentPage + 1) - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.4f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        val scale = lerp(
                            start = 1f, stop = 1.5f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page - 1)
                        }
                    },

                    text = String.format(
                        Locale.Builder().setLocale(Locale.getDefault()).build(), "%02d", content
                    ),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

    }

    // Collect the pager state in a snapshot flow
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.filter { it != previousPage }  // Only process changes
            .collect { newPage ->
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                val jump = abs(newPage - previousPage)
                onMovement(previousPage < newPage, jump)
                previousPage = newPage
            }
    }
}


@Composable
fun VerticalWheelSpinner(
    modifier: Modifier = Modifier,
    items: List<String>,
    defaultSelected: String?,
    onMovement: (selectedOption: String) -> Unit = { _ -> },
) {

    val coroutineScope = rememberCoroutineScope()

    val view = LocalView.current

    val height = 100.dp

    val cellSize = height / 3

    val numbers = listOf(" ") + items + listOf(" ")

    val pagerState =
        rememberPagerState(items.indexOf(defaultSelected).takeIf { it != -1 } ?: 0) { numbers.size }

    var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }

    // Listen to defaultSelected changes
    LaunchedEffect(defaultSelected) {
        val newPage = items.indexOf(defaultSelected).takeIf { it != -1 } ?: 0
        if (newPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(newPage)
        }
    }

    VerticalPager(
        modifier = modifier.height(height),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = pagerState,
        pageSize = PageSize.Fixed(cellSize),
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(10),
            snapAnimationSpec = tween(),
        )

    ) { page ->

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cellSize), contentAlignment = Alignment.Center
        ) {

            val content = numbers[page]

            if (content.isNotBlank()) {

                Text(modifier = Modifier
                    .graphicsLayer {
                        val pageOffset =
                            (((pagerState.currentPage + 1) - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.4f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        val scale = lerp(
                            start = 1f, stop = 1.5f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = scale
                        scaleY = scale
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page - 1)
                        }
                    },

                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }

    // Collect the pager state in a snapshot flow
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.filter { it != previousPage }  // Only process changes
            .collect { newPage ->
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onMovement(items[newPage])
                previousPage = newPage
            }
    }
}