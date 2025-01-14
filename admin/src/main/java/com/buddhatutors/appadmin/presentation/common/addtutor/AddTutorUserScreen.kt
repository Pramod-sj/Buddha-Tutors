@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)

package com.buddhatutors.appadmin.presentation.common.addtutor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.appadmin.Constant.EXTRA_TUTOR_CHANGED
import com.buddhatutors.auth.presentation.termconditions.EXTRA_IS_ACCEPTED
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.FullScreenLoader
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.StartEndTimeSelectComposable
import com.buddhatutors.common.MultiSelectionDialog
import com.buddhatutors.common.TextFieldChipHolder
import com.buddhatutors.common.domain.model.TimeSlot
import com.buddhatutors.common.domain.model.Topic
import com.buddhatutors.common.domain.model.user.UserType
import kotlinx.coroutines.launch
import java.util.Locale

@Preview
@Composable
internal fun PreviewAddTutorUserScreen() {
    AddUserScreenContent(
        modifier = Modifier.background(Color.White),
        uiState = AddUserUiState(
            topics = listOf(Topic("", "Testing 1", true)),
        ),
        uiEvent = {}
    )
}

@Composable
fun AddTutorUserScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<AddTutorUserViewModel>()

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
                            text = "Add tutor"
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
                        viewModel.setEvent(AddUserUiEvent.OnAddUserClick)
                    },
                    enabled = uiState.isRegistrationValid
                ) {
                    Text(text = "Save tutor")
                }
            }
        }
    ) {

        AddUserScreenContent(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            uiState = uiState,
            uiEvent = viewModel::setEvent
        )

    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { uiEffect ->
            when (uiEffect) {
                is AddUserUiEffect.ShowErrorMessage -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = uiEffect.message)
                    }
                }

                AddUserUiEffect.ShowAddUserSuccess -> {

                    navigator.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(EXTRA_TUTOR_CHANGED, true)

                    navigator.popBackStack()
                }
            }
        }
    }

    if (uiState.isTimepickerDialogVisible) {

        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            dragHandle = null,
            onDismissRequest = {
                viewModel.setEvent(AddUserUiEvent.UpdateTimePickerDialogVisibility(false))
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
                            AddUserUiEvent.OnTimeSlotAdded(
                                TimeSlot(
                                    start = startTime,
                                    end = endTime
                                )
                            )
                        )

                        modalBottomSheetState.hide()

                        viewModel.setEvent(AddUserUiEvent.UpdateTimePickerDialogVisibility(false))
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
                viewModel.setEvent(AddUserUiEvent.UpdateTopicsDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(AddUserUiEvent.UpdateTopicsDialogVisibility(false))
                viewModel.setEvent(AddUserUiEvent.OnExpertiseTopicChanged(it))
            }
        )
    }

    if (uiState.isLanguageSelectionDialogVisible) {
        MultiSelectionDialog(
            title = "Select languages",
            selectedValues = uiState.selectedLanguage,
            options = uiState.languages,
            onDismissRequest = {
                viewModel.setEvent(AddUserUiEvent.UpdateLanguageDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(AddUserUiEvent.OnLanguageSelectionChanged(it))
            }
        )
    }

    if (uiState.isDaySelectionDialogVisible) {
        MultiSelectionDialog(
            title = "Select days of availability",
            selectedValues = uiState.selectedAvailabilityDay,
            options = uiState.days.map { it.second },
            onDismissRequest = {
                viewModel.setEvent(AddUserUiEvent.UpdateDaysDialogVisibility(false))
            },
            onConfirmClick = {
                viewModel.setEvent(AddUserUiEvent.OnDayAvailabilityChanged(it))
            }
        )
    }

    FullScreenLoader(uiState.showFullScreenLoader)
}


@Composable
internal fun AddUserScreenContent(
    modifier: Modifier,
    uiState: AddUserUiState,
    uiEvent: (AddUserUiEvent) -> Unit
) {

    var showTopicDialog by remember { mutableStateOf(false) }

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
            onValueChange = { text ->
                uiEvent(AddUserUiEvent.OnUserNameChanged(text))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
            ),
            shape = MaterialTheme.shapes.small,
            colors = DefaultBuddhaTutorTextFieldColors,
            isError = uiState.validationResult?.isNameValid == false
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small),
            value = uiState.email.orEmpty(),
            label = {
                Text(text = "Email")
            },
            onValueChange = { text ->
                uiEvent(AddUserUiEvent.OnEmailChanged(text))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            shape = MaterialTheme.shapes.small,
            colors = DefaultBuddhaTutorTextFieldColors,
            isError = uiState.validationResult?.isEmailValid == false
        )

        Column {

            //wrap inside a column because there was a jump when view is hidden in AnimatedVisibility

            AnimatedVisibility(
                visible = uiState.userType == UserType.TUTOR
            ) {


                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Column(modifier = Modifier.fillMaxWidth()) {

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Expertise in ",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable { showTopicDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextFieldChipHolder(
                            label = "Click here to add topics",
                            selectedValues = uiState.selectedTopics.map { it.label },
                            onClick = {
                                uiEvent(AddUserUiEvent.UpdateTopicsDialogVisibility(true))
                            },
                            onRemoveChipClick = { chipLabel ->
                                uiState.topics.find { it.label == chipLabel }
                                    ?.let {
                                        uiEvent(AddUserUiEvent.OnTopicItemRemoved(it))
                                    }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Speaks in",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable { showTopicDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextFieldChipHolder(
                            label = "Click here to add languages",
                            selectedValues = uiState.selectedLanguage,
                            onClick = {
                                uiEvent(AddUserUiEvent.UpdateLanguageDialogVisibility(true))
                            },
                            onRemoveChipClick = { chipLabel ->
                                //uiEvent(AddUserUiEvent.OnLanguageSelectionChanged(it))
                            }
                        )

                        /*MultiSelectTextFieldDropdown(
                            modifier = Modifier.fillMaxWidth(),
                            selectedValues = uiState.selectedLanguage,
                            options = uiState.languages,
                            label = "Select language of teaching",
                            onValueChangedEvent = { value ->
                                uiEvent(AddUserUiEvent.OnLanguageSelectionChanged(value))
                            },
                            dropDownHeightInDp = 200.dp
                        )*/

                    }

                    Column {

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Please choose availability",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextFieldChipHolder(
                            selectedValues = uiState.selectedAvailabilityDay,
                            label = "Click here to add available days",
                            onClick = {
                                uiEvent(AddUserUiEvent.UpdateDaysDialogVisibility(true))
                            },
                            onRemoveChipClick = {

                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TextFieldChipHolder(
                            label = "Click here to add timeslots",
                            selectedValues = uiState.selectedTimeSlots.map { "${it.start} - ${it.end}" },
                            onClick = {
                                uiEvent(AddUserUiEvent.UpdateTimePickerDialogVisibility(true))
                            },
                            onRemoveChipClick = {
                                val timeSlotString = it.split("-").map { it.trim() }
                                uiEvent(
                                    AddUserUiEvent.OnTimeSlotRemoved(
                                        TimeSlot(
                                            start = timeSlotString.getOrNull(0),
                                            end = timeSlotString.getOrNull(1)
                                        )
                                    )
                                )
                            }
                        )


                        /* AvailabilityCalendarChooser(
                     selectedDays = uiState.selectedAvailabilityDay,
                     selectedTimeSlot = uiState.selectedTimeSlot,
                     onDaysSelectionChangeEvent = { days ->
                         uiEvent(RegisterUiEvent.OnDayAvailabilityChanged(days))
                     },
                     onTimeSelectionChangeEvent = { timeSlot ->
                         uiEvent(RegisterUiEvent.OnTimeAvailabilityChanged(timeSlot))
                     }
                 )*/
                    }

                    Spacer(Modifier.height(16.dp))

                }
            }

        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectTextFieldDropdown(
    selectedValues: List<String>,
    options: List<String>,
    label: String,
    onValueChangedEvent: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    dropDownHeightInDp: Dp? = null
) {

    var expanded by remember { mutableStateOf(false) }

    val mSelectedValues =
        remember(selectedValues) { mutableStateListOf(*selectedValues.toTypedArray()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .heightIn(min = 40.dp)
                .then(
                    if (mSelectedValues.isEmpty()) Modifier.padding(16.dp)
                    else Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                .fillMaxWidth()
        ) {
            if (mSelectedValues.isEmpty()) {
                Text(label, Modifier.align(Alignment.CenterStart))
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mSelectedValues.forEach { value ->
                        ElevatedAssistChip(
                            border = null,
                            onClick = {
                                if (mSelectedValues.contains(value)) {
                                    mSelectedValues.remove(value)
                                } else {
                                    mSelectedValues.add(value)
                                }
                            },
                            label = {
                                Text(value)
                            },
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Remove"
                                )
                            }
                        )
                    }
                }
            }
            ExposedDropdownMenuDefaults.TrailingIcon(
                modifier = Modifier.align(Alignment.CenterEnd),
                expanded = expanded
            )
        }

        ExposedDropdownMenu(
            modifier = Modifier
                .then(dropDownHeightInDp?.let { Modifier.heightIn(max = it) } ?: Modifier),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = option in mSelectedValues,
                                onCheckedChange = {}
                            )
                            Text(text = option)
                        }
                    },
                    onClick = {
                        if (mSelectedValues.contains(option)) {
                            mSelectedValues.remove(option)
                        } else {
                            mSelectedValues.add(option)
                        }
                        //expanded = false
                    }
                )
            }
        }
    }

    LaunchedEffect(mSelectedValues) {

        snapshotFlow { mSelectedValues.toList() } // Convert to a list to detect content changes
            .collect { updatedValues -> onValueChangedEvent(updatedValues) }

    }
}