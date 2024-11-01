@file:OptIn(ExperimentalLayoutApi::class)

package com.buddhatutors.framework.ui.auth.register.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.framework.ui.auth.register.DynamicSelectTextField

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewAvailabilityCalendar() {
    AvailabilityCalendarChooser(
        onDaysSelectionChangeEvent = {},
        onTimeSelectionChangeEvent = {}
    )
}

@Composable
fun AvailabilityCalendarChooser(
    selectedDays: List<String> = emptyList(),
    selectedTimeSlot: TimeSlot? = null,
    onDaysSelectionChangeEvent: (List<String>) -> Unit,
    onTimeSelectionChangeEvent: (TimeSlot) -> Unit
) {

    var mSelectedDays by remember(selectedDays) { mutableStateOf(selectedDays) }

    var mSelectedTimeSlot by remember(selectedTimeSlot) { mutableStateOf(selectedTimeSlot) }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val times = (8..23).flatMap {
        listOf("${String.format("%02d", it)}:00", "${String.format("%02d", it)}:30")
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        Text("Please choose your availability")

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy((-2).dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            days.forEach { day ->
                FilterChip(
                    modifier = Modifier.padding(0.dp),
                    label = { Text(text = day) },
                    selected = day in selectedDays,
                    onClick = {
                        mSelectedDays = if (mSelectedDays.contains(day)) {
                            mSelectedDays - day
                        } else {
                            mSelectedDays + day
                        }
                    },
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            DynamicSelectTextField(
                modifier = Modifier.weight(0.5f),
                selectedValue = selectedTimeSlot?.start.orEmpty(),
                options = times,
                label = "Start time",
                onValueChangedEvent = {
                    mSelectedTimeSlot =
                        mSelectedTimeSlot?.copy(start = it) ?: TimeSlot(start = it, end = null)
                }
            )

            Text(
                text = "to"
            )

            DynamicSelectTextField(
                modifier = Modifier.weight(0.5f),
                selectedValue = selectedTimeSlot?.end.orEmpty(),
                options = times,
                label = "End time",
                onValueChangedEvent = {
                    mSelectedTimeSlot =
                        mSelectedTimeSlot?.copy(end = it) ?: TimeSlot(start = null, end = it)
                }
            )
        }
    }

    LaunchedEffect(mSelectedDays) {
        onDaysSelectionChangeEvent(mSelectedDays.toList())
    }

    LaunchedEffect(mSelectedTimeSlot) {
        mSelectedTimeSlot?.let { onTimeSelectionChangeEvent(it) }
    }

}