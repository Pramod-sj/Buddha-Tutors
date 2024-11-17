@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.appadmin.tutorverification

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.theme.BuddhaTutorTheme

@Preview
@Composable
fun PreviewTutorDetailPage() {
    BuddhaTutorTheme {
        TutorVerificationScreenContent(
            uiState = TutorVerificationUiState(),
            uiEvent = {})
    }
}

@Composable
fun TutorVerificationScreen() {

    val viewModel = hiltViewModel<TutorVerificationViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    TutorVerificationScreenContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent
    )

}

@Composable
internal fun TutorVerificationScreenContent(
    uiState: TutorVerificationUiState, uiEvent: (TutorDetailUiEvent) -> Unit
) {
    Scaffold(topBar = {
        LargeTopAppBar(title = {
            Text(text = uiState.tutorListing?.tutorUser?.name ?: "")
        }, navigationIcon = {
            com.buddhatutors.common.ActionIconButton(
                imageVector = Icons.Filled.ArrowBack, iconTint = Color.Black
            ) {
                //      navigator.popBackStack()
            }
        })
    }, bottomBar = {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f), Color.White
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            if (uiState.isRejectButtonVisible) {

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f),
                    onClick = { uiEvent(TutorDetailUiEvent.RejectTutorButtonClick) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        disabledContentColor = MaterialTheme.colorScheme.errorContainer.copy(0.4f),
                        disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                            0.4f
                        ),
                    )
                ) {
                    Text(text = "Reject")
                }

            }

            if (uiState.isApproveButtonVisible) {

                Button(modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                    onClick = { uiEvent(TutorDetailUiEvent.ApproveTutorButtonClick) }) {
                    Text(text = "Approve")
                }

            }

        }
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {

            Text("I'm a pass out willing to teach student who are super interested in getting the knowlege of ")

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Text("Expertise in", style = MaterialTheme.typography.titleMedium)

            uiState.tutorListing?.expertiseIn?.forEach {
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Text(text = it.label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Text("Day and Hours availability", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.tutorListing?.availableDays?.forEach {
                    SuggestionChip({}, { Text(it) })
                }
            }


        }
    }
}