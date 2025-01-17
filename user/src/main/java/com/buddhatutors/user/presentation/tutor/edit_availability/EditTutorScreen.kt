@file:OptIn(
    ExperimentalMaterial3Api::class
)

package com.buddhatutors.user.presentation.tutor.edit_availability

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.FullScreenLoader
import com.buddhatutors.common.MultiSelectionDialog
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.StartEndTimeSelectComposable
import com.buddhatutors.common.TextFieldChipHolder
import com.buddhatutors.common.domain.model.TimeSlot
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.messaging.MessageHelper
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewAddTutorUserScreen() {
    EditTutorScreenContent(
        modifier = Modifier.background(Color.White),
        uiState = EditTutorUiState(
            topics = listOf(Topic("", "Testing 1", true)),
        ),
        uiEvent = {}
    )
}

@Composable
fun EditTutorScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<EditTutorViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MaterialTheme(
                typography = Typography(
                    titleLarge = MaterialTheme.typography.titleLarge,
                    headlineSmall = MaterialTheme.typography.headlineMedium,
                )
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = "Update your data"
                        )
                    },
                    navigationIcon = {
                        ActionIconButton(
                            imageVector = Icons.Filled.ArrowBack,
                            iconTint = Color.Black
                        ) {
                            navigator.popBackStack()
                        }
                    })
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        bottomBar = {
            Column(
                Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setEvent(EditTutorUiEvent.UpdateButtonClick)
                    },
                    enabled = uiState.isRegistrationValid
                ) {
                    Text(text = "Save tutor")
                }
            }
        }
    ) {

        EditTutorScreenContent(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            uiState = uiState,
            uiEvent = viewModel::setEvent
        )

    }

    if (uiState.isTimepickerDialogVisible) {

        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            dragHandle = null,
            onDismissRequest = {
                viewModel.setEvent(EditTutorUiEvent.UpdateTimePickerDialogVisibility(false))
            },
            shape = MaterialTheme.shapes.medium.copy(
                bottomEnd = CornerSize(0.dp),
                bottomStart = CornerSize(0.dp)
            )
        ) {
            StartEndTimeSelectComposable(
                selectedStartTime = null,
                selectedEndTime = null,
                onTimeSlotSelected = { startTime, endTime ->

                    coroutineScope.launch {

                        viewModel.setEvent(
                            EditTutorUiEvent.OnTimeSlotAdded(
                                TimeSlot(
                                    start = startTime,
                                    end = endTime
                                )
                            )
                        )

                        modalBottomSheetState.hide()

                        viewModel.setEvent(EditTutorUiEvent.UpdateTimePickerDialogVisibility(false))
                    }
                }
            )
        }
    }

    if (uiState.isTopicSelectionDialogVisible) {
        MultiSelectionDialog(
            title = "Select topics",
            selectedValues = uiState.selectedTopics.map { it.label },
            options = uiState.topics.map { it.label },
            onDismissRequest = {
                viewModel.setEvent(EditTutorUiEvent.UpdateTopicsDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(EditTutorUiEvent.UpdateTopicsDialogVisibility(false))
                viewModel.setEvent(EditTutorUiEvent.OnExpertiseTopicChanged(it))
            }
        )
    }

    if (uiState.isLanguageSelectionDialogVisible) {
        MultiSelectionDialog(
            title = "Select languages",
            selectedValues = uiState.selectedLanguage,
            options = uiState.languages,
            onDismissRequest = {
                viewModel.setEvent(EditTutorUiEvent.UpdateLanguageDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(EditTutorUiEvent.OnLanguageSelectionChanged(it))
            }
        )
    }

    if (uiState.isDaySelectionDialogVisible) {
        MultiSelectionDialog(
            title = "Select days of availability",
            selectedValues = uiState.selectedAvailabilityDay,
            options = uiState.days.map { it.second },
            onDismissRequest = {
                viewModel.setEvent(EditTutorUiEvent.UpdateDaysDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(EditTutorUiEvent.OnDayAvailabilityChanged(it))
            }
        )
    }

    FullScreenLoader(uiState.showFullScreenLoader)
}

@Composable
internal fun EditTutorScreenContent(
    modifier: Modifier,
    uiState: EditTutorUiState,
    uiEvent: (EditTutorUiEvent) -> Unit
) {


    if (uiState.showInitialLoader) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        }

    } else {

        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                value = uiState.name.orEmpty(),
                label = {
                    Text(text = "Name")
                },
                onValueChange = { },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                ),
                shape = MaterialTheme.shapes.small,
                colors = DefaultBuddhaTutorTextFieldColors,
                enabled = false
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                value = uiState.email.orEmpty(),
                label = {
                    Text(text = "Email")
                },
                onValueChange = { },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                shape = MaterialTheme.shapes.small,
                colors = DefaultBuddhaTutorTextFieldColors,
                enabled = false
            )

            //wrap inside a column because there was a jump when view is hidden in AnimatedVisibility
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(modifier = Modifier.fillMaxWidth()) {

                    HeaderWithEditButton(
                        label = "Expertise in",
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateTopicsDialogVisibility(true))
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    TextFieldChipHolder(
                        label = "Click here to add topics",
                        selectedValues = uiState.selectedTopics.map { it.label },
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateTopicsDialogVisibility(true))
                        },
                        onRemoveChipClick = { chipLabel ->
                            uiState.topics.find { it.label == chipLabel }
                                ?.let {
                                    uiEvent(EditTutorUiEvent.OnTopicItemRemoved(it))
                                }
                        }
                    )

                }

                Column(modifier = Modifier.fillMaxWidth()) {

                    HeaderWithEditButton(
                        label = "Speaks in",
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateLanguageDialogVisibility(true))
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    TextFieldChipHolder(
                        label = "Click here to add languages",
                        selectedValues = uiState.selectedLanguage,
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateLanguageDialogVisibility(true))
                        },
                        onRemoveChipClick = { chipLabel ->
                            uiState.languages.find { it == chipLabel }
                                ?.let {
                                    uiEvent(EditTutorUiEvent.OnLanguageItemRemoved(it))
                                }
                        }
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {

                    HeaderWithEditButton(
                        label = "Day availability",
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateDaysDialogVisibility(true))
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    TextFieldChipHolder(
                        selectedValues = uiState.selectedAvailabilityDay,
                        label = "Click here to add available days",
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateDaysDialogVisibility(true))
                        },
                        onRemoveChipClick = { chipLabel ->
                            uiState.days.find { it.second == chipLabel }
                                ?.let {
                                    uiEvent(EditTutorUiEvent.OnDayAvailabilityRemoved(it.second))
                                }

                        }
                    )

                }

                Column(modifier = Modifier.fillMaxWidth()) {

                    HeaderWithEditButton(
                        label = "Time availability",
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateTimePickerDialogVisibility(true))
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    TextFieldChipHolder(
                        label = "Click here to add timeslots",
                        selectedValues = uiState.selectedTimeSlots.map { "${it.start} - ${it.end}" },
                        onClick = {
                            uiEvent(EditTutorUiEvent.UpdateTimePickerDialogVisibility(true))
                        },
                        onRemoveChipClick = {
                            val timeSlotString = it.split("-").map { it.trim() }
                            uiEvent(
                                EditTutorUiEvent.OnTimeSlotRemoved(
                                    TimeSlot(
                                        start = timeSlotString.getOrNull(0),
                                        end = timeSlotString.getOrNull(1)
                                    )
                                )
                            )
                        }
                    )
                }

            }

        }

    }

}

@Composable
fun HeaderWithEditButton(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable { onClick() }
        )

        Spacer(Modifier.weight(1f))

        ActionIconButton(
            imageVector = Icons.Outlined.Edit,
            iconTint = Color.Black
        ) {
            onClick()
        }

    }
}