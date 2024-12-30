@file:OptIn(ExperimentalComposeUiApi::class)

package com.buddhatutors.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Preview
@Composable
fun PreviewStartEndTimeSelectComposable() {
    StartEndTimeSelectComposable(
        onTimeSlotSelected = { _, _ ->

        }
    )
}

@Composable
fun StartEndTimeSelectComposable(
    selectedStartTime: String? = null,
    selectedEndTime: String? = null,
    onTimeSlotSelected: (startTime: String, endTime: String) -> Unit
) {

    val startTimes = remember {
        (8..23).flatMap {
            listOf(
                "${String.format(Locale.ENGLISH, "%02d", it)}:00",
                "${String.format(Locale.ENGLISH, "%02d", it)}:30"
            )
        }.toMutableList().let { list ->
            list.removeAt(list.size - 1)
            list
        }.map { timeString ->
            val (hour, min) = timeString.split(":").map { it.toInt() }
            val hour12 = if (hour > 12) hour - 12 else hour
            val period = if (hour < 12) "AM" else "PM"
            String.format(Locale.ENGLISH, "%02d:%02d %s", hour12, min, period)
        }
    }

    val endTimes = remember {
        (8..23).flatMap {
            listOf(
                "${String.format(Locale.ENGLISH, "%02d", it)}:00",
                "${String.format(Locale.ENGLISH, "%02d", it)}:30"
            )
        }.toMutableList().let { list ->
            list.removeAt(0)
            list
        }.map { timeString ->
            val (hour, min) = timeString.split(":").map { it.toInt() }
            val hour12 = if (hour > 12) hour - 12 else hour
            val period = if (hour < 12) "AM" else "PM"
            String.format(Locale.ENGLISH, "%02d:%02d %s", hour12, min, period)
        }
    }

    var mSelectedStartTime by remember(selectedStartTime) {
        if (selectedEndTime == null && startTimes.isNotEmpty()) {
            mutableStateOf(startTimes.firstOrNull())
        } else {
            mutableStateOf(selectedStartTime)
        }
    }

    var mSelectedEndTime by remember(selectedEndTime) {
        if (selectedEndTime == null && endTimes.isNotEmpty()) {
            mutableStateOf(endTimes.firstOrNull())
        } else {
            mutableStateOf(selectedEndTime)
        }
    }

    val defaultEndTime by remember {
        derivedStateOf {
            startTimes.indexOfFirst { it == mSelectedStartTime }
                .coerceIn(0, endTimes.size - 1)
                .let { endTimes.getOrNull(it) }
        }
    }

    val isButtonValid = remember(mSelectedStartTime, mSelectedEndTime) {
        isTimeSlotValid(
            mSelectedStartTime,
            mSelectedEndTime
        )
    }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {

        Text(
            modifier = Modifier.padding(16.dp),
            text = "Choose timeslot",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            VerticalWheelSpinner(
                modifier = Modifier.weight(0.5f),
                items = startTimes,
                defaultSelected = selectedStartTime,
                onMovement = { selectedOption ->
                    mSelectedStartTime = selectedOption
                }
            )


            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Array"
            )

            VerticalWheelSpinner(
                modifier = Modifier.weight(0.5f),
                items = endTimes,
                defaultSelected = defaultEndTime,
                onMovement = { selectedOption ->
                    mSelectedEndTime = selectedOption
                }
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {

                mSelectedStartTime?.let { s ->
                    mSelectedEndTime?.let { e ->
                        onTimeSlotSelected(s, e)
                    }
                }

            }, enabled = isButtonValid
        ) {
            Text("Add time slot")
        }
    }
}

private fun isTimeSlotValid(start: String?, end: String?): Boolean {
    if (start == null || end == null) return false

    return try {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val startTime = formatter.parse(start)
        val endTime = formatter.parse(end)
        startTime?.before(endTime) ?: false
    } catch (_: Exception) {
        false
    }
}