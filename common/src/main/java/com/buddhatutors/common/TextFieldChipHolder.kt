@file:OptIn(ExperimentalLayoutApi::class)

package com.buddhatutors.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@Composable
fun TextFieldChipHolder(
    label: String,
    selectedValues: List<String>,
    onClick: () -> Unit,
    onRemoveChipClick: (chip: String) -> Unit,
    trailingIcon: @Composable BoxScope.() -> Unit = {}
) {
    val mSelectedValues =
        remember(selectedValues) { mutableStateListOf(*selectedValues.toTypedArray()) }

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(0.5f),
                shape = MaterialTheme.shapes.small
            )
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onClick() }
            .then(
                if (mSelectedValues.isEmpty()) Modifier.padding(16.dp, 8.dp)
                else Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            .heightIn(min = 80.dp)
            .fillMaxWidth()
    ) {
        if (mSelectedValues.isEmpty()) {
            Text(
                text = label,
                Modifier
                    .align(Alignment.Center)
                    .alpha(0.4f)
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                mSelectedValues.forEach { value ->
                    ElevatedAssistChip(
                        modifier = Modifier.clickable(enabled = false, onClick = {}),
                        border = null,
                        onClick = {},
                        label = {
                            Text(value)
                        },
                        trailingIcon = {
                            Icon(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        onRemoveChipClick(value)
                                    },
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Remove",
                            )
                        }
                    )
                }
            }
        }
        trailingIcon()
    }
}