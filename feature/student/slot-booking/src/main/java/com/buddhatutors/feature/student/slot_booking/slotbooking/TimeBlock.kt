package com.buddhatutors.feature.student.slot_booking.slotbooking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buddhatutors.feature.student.slot_booking.SlotTimeUiModel


@Preview
@Composable
internal fun PreviewTimeBlock() {
    TimeBlock(
        slotTimeUiModel = SlotTimeUiModel(
            dateString = "",
            startTime = "",
            endTime = "",
            isSlotBooked = false
        ),
        isSelected = true
    ) { }
}


@Composable
internal fun TimeBlock(
    modifier: Modifier = Modifier,
    slotTimeUiModel: SlotTimeUiModel,
    isSelected: Boolean,
    onClick: (slotTimeUiModel: SlotTimeUiModel) -> Unit
) {

    Card(
        modifier = modifier.alpha(if (slotTimeUiModel.isSlotBooked) 0.6f else 1f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(0.2f)
        ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        enabled = !slotTimeUiModel.isSlotBooked,
        onClick = { onClick(slotTimeUiModel) },
    ) {
        Text(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            text = "${slotTimeUiModel.startTime} - ${slotTimeUiModel.endTime}",
            style = MaterialTheme.typography.bodySmall.copy(color = if (isSelected) Color.White else Color.Black),
            textAlign = TextAlign.Center
        )
    }

}