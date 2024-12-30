@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun GenericBottomSheet(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit, // Action when button is clicked
    onDismissRequest: () -> Unit // Action when bottom sheet is dismissed
) {

    val state = rememberModalBottomSheetState()

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = state,
        dragHandle = null,
        onDismissRequest = { onDismissRequest() }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Action Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        onButtonClick()
                        state.hide()
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGenericBottomSheet() {
    GenericBottomSheet(
        title = "Important Notice",
        description = "Please confirm your action. This is an example of a generic bottom sheet.",
        buttonText = "Confirm",
        onButtonClick = {
            // Handle button click logic here
        },
        onDismissRequest = {
            // Handle bottom sheet dismissal logic here
        }
    )
}
