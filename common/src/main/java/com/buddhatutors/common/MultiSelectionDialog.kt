package com.buddhatutors.common

import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.absoluteValue

@Composable
fun MultiSelectionDialog(
    title: String,
    selectedValues: List<String>,
    options: List<String>,
    onDismissRequest: () -> Unit,
    onConfirmClick: (List<String>) -> Unit
) {

    val mSelectedValues: SnapshotStateList<String> =
        remember(selectedValues) { mutableStateListOf(*selectedValues.toTypedArray()) }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .heightIn(max = (LocalConfiguration.current.screenHeightDp.absoluteValue * 0.7f).dp),
        title = {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        },
        confirmButton = {
            Button(onClick = {
                onConfirmClick(mSelectedValues)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                mSelectedValues.clear()
            }) {
                Text("Reset")
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(options) { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (mSelectedValues.contains(option)) {
                                    mSelectedValues.remove(option)
                                } else {
                                    mSelectedValues.add(option)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = option in mSelectedValues,
                            onCheckedChange = {
                                if (mSelectedValues.contains(option)) {
                                    mSelectedValues.remove(option)
                                } else {
                                    mSelectedValues.add(option)
                                }
                            }
                        )
                        Text(text = option)
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}
