@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.feature.admin.add_topic

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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.Navigator

@Preview
@Composable
fun PreviewAddTopicScreen() {

    AddTopicScreenContent(uiState = AddTopicUiState(
        topicText = null,
        isTopicEnteredValid = false
    ), uiEvent = {})
}

@Composable
fun AddTopicScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<AddTopicViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = {
            navigator.popBackStack()
        }
    ) {

        AddTopicScreenContent(
            uiState = uiState,
            uiEvent = viewModel::setEvent
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                AddTopicUiEffect.AddTopicSuccess -> {
                    navigator.popBackStack()
                }
            }
        }
    }
}

@Composable
internal fun AddTopicScreenContent(
    uiState: AddTopicUiState,
    uiEvent: (AddTopicUiEvent) -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "Add topic",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.topicText.orEmpty(),
            onValueChange = { text ->
                uiEvent(AddTopicUiEvent.OnTopicTextChanged(text))
            },
            shape = MaterialTheme.shapes.small,
            colors = DefaultBuddhaTutorTextFieldColors,
            placeholder = { Text("Enter topic description here") },
            isError = uiState.isTopicEnteredValid == false,
            minLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { uiEvent(AddTopicUiEvent.AddTopicButton) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}