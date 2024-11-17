package com.buddhatutors.common.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buddhatutors.common.VerticalWheelSpinner
import java.text.SimpleDateFormat
import java.util.Locale

@Preview
@Composable
fun PreviewStartEndTimeSelectComposable() {
    StartEndTimeSelectComposable(
        times = listOf("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM"),
        onTimeSlotSelected = { _, _ ->

        }
    )
}

@Composable
fun StartEndTimeSelectComposable(
    times: List<String>,
    selectedStartTime: String? = null,
    selectedEndTime: String? = null,
    onTimeSlotSelected: (startTime: String, endTime: String) -> Unit
) {

    var mSelectedStartTime by remember(selectedStartTime) {
        if (selectedEndTime == null && times.isNotEmpty()) {
            mutableStateOf(times.firstOrNull())
        } else {
            mutableStateOf(selectedStartTime)
        }
    }

    var mSelectedEndTime by remember(selectedEndTime) {
        if (selectedEndTime == null && times.isNotEmpty()) {
            mutableStateOf(times.firstOrNull())
        } else {
            mutableStateOf(selectedEndTime)
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
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            VerticalWheelSpinner(
                modifier = Modifier.weight(0.5f),
                items = times,
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
                items = times,
                defaultSelected = selectedEndTime,
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