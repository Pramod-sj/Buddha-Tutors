@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.common.auth.ui.register

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.auth.ui.termconditions.EXTRA_IS_ACCEPTED
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.registration.TimeSlot
import com.buddhatutors.domain.model.user.UserType
import kotlinx.coroutines.launch
import java.util.Locale

@Preview
@Composable
internal fun PreviewRegisterPage() {
    RegisterScreenContent(
        modifier = Modifier.background(Color.White),
        uiState = RegisterUiState(
            topics = listOf(Topic("", "Testing 1", true)),
        ),
        uiEvent = {}
    )
}

@Composable
fun RegisterScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<RegisterViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    // Retrieve the saved state handle from the previous back stack entry
    val savedStateHandle = navigator.currentBackStackEntry?.savedStateHandle

    val termConditionResultState by savedStateHandle
        ?.getStateFlow(EXTRA_IS_ACCEPTED, false)
        ?.collectAsState(false) ?: remember { mutableStateOf(false) }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val times = remember {
        (8..23).flatMap {
            listOf(
                "${String.format(Locale.ENGLISH, "%02d", it)}:00",
                "${String.format(Locale.ENGLISH, "%02d", it)}:30"
            )
        }.map { timeString ->
            val (hour, min) = timeString.split(":").map { it.toInt() }
            val hour12 = if (hour > 12) hour - 12 else hour
            val period = if (hour < 12) "AM" else "PM"
            String.format(Locale.ENGLISH, "%02d:%02d %s", hour12, min, period)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "Create your account",
                        style = MaterialTheme.typography.headlineMedium
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
                        viewModel.setEvent(RegisterUiEvent.OnRegisterClick)
                    },
                    enabled = uiState.isRegistrationValid
                ) {
                    if (uiState.isRegistrationInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Register")
                    }
                }
            }
        }
    ) {

        RegisterScreenContent(
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
                is RegisterUiEffect.ShowErrorMessage -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = uiEffect.message)
                    }
                }

                RegisterUiEffect.ShowRegisterSuccess -> {
                    navigator.popBackStack()
                }

                is RegisterUiEffect.NavigateToTermAndConditionPage -> {
                    navigator.navigate(AuthGraph.TermAndConditions)
                }
            }
        }
    }

    LaunchedEffect(termConditionResultState) {
        viewModel.setEvent(RegisterUiEvent.OnTermsAndConditionsStateChanged(termConditionResultState))
    }

    if (uiState.isTimepickerDialogVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.setEvent(RegisterUiEvent.UpdateTimePickerDialogVisibility(false))
            },
            shape = MaterialTheme.shapes.medium.copy(
                bottomEnd = CornerSize(0.dp),
                bottomStart = CornerSize(0.dp)
            )
        ) {
            StartEndTimeSelectComposable(
                times = times,
                selectedStartTime = null,
                selectedEndTime = null,
                onTimeSlotSelected = { startTime, endTime ->

                    viewModel.setEvent(
                        RegisterUiEvent.OnTimeSlotAdded(
                            TimeSlot(
                                start = startTime,
                                end = endTime
                            )
                        )
                    )
                }
            )
        }
    }
}


@Composable
internal fun RegisterScreenContent(
    modifier: Modifier,
    uiState: RegisterUiState,
    uiEvent: (RegisterUiEvent) -> Unit
) {

    var showTopicDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.name.orEmpty(),
            label = {
                Text(text = "Name")
            },
            onValueChange = { text ->
                uiEvent(RegisterUiEvent.OnUserNameChanged(text))
            },
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            isError = uiState.validationResult?.isNameValid == false
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.email.orEmpty(),
            label = {
                Text(text = "Email")
            },
            onValueChange = { text ->
                uiEvent(RegisterUiEvent.OnEmailChanged(text))
            },
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            isError = uiState.validationResult?.isEmailValid == false
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password.orEmpty(),
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text(text = "Password")
            },
            onValueChange = { text ->
                uiEvent(RegisterUiEvent.OnPasswordChanged(text))
            },
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            isError = uiState.validationResult?.isPasswordValid == false
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.confirmPassword.orEmpty(),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { text ->
                uiEvent(RegisterUiEvent.OnConfirmPasswordChanged(text))
            },
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            label = {
                Text("Confirm Password")
            },
            isError = uiState.validationResult?.isConfirmPasswordValid == false
        )

        Column {

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "How you want to register yourself?",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                UserType.entries.take(2).forEach { text ->
                    Row(
                        Modifier
                            .weight(0.5f)
                            .selectable(
                                selected = (text == uiState.userType),
                                onClick = {
                                    uiEvent(RegisterUiEvent.OnUserTypeSelected(text))
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = text == uiState.userType,
                            onClick = { uiEvent(RegisterUiEvent.OnUserTypeSelected(text)) }
                        )
                        Text(text = text.value)
                    }
                }

            }
        }

        AnimatedVisibility(uiState.userType == UserType.TUTOR) {

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(modifier = Modifier.fillMaxWidth()) {

                    if (uiState.topics.isNotEmpty()) {

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Expertise in ",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable { showTopicDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MultiSelectTextFieldDropdown(
                            modifier = Modifier.fillMaxWidth(),
                            selectedValues = uiState.selectedTopics.map { topic -> topic.label },
                            options = uiState.topics.map { topic -> topic.label },
                            label = "Select topics",
                            onValueChangedEvent = { value ->
                                uiEvent(RegisterUiEvent.OnExpertiseTopicChanged(value))
                            },
                            dropDownHeightInDp = 200.dp
                        )

                    }

                }

                Column {

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Please choose your availability",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MultiSelectTextFieldDropdown(
                        modifier = Modifier.fillMaxWidth(),
                        selectedValues = uiState.selectedAvailabilityDay,
                        options = uiState.days.map { it.second },
                        label = "Select days",
                        onValueChangedEvent = { value ->
                            uiEvent(RegisterUiEvent.OnDayAvailabilityChanged(value))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextFieldChipHolder(
                        label = "Select timeslots",
                        selectedValues = uiState.selectedTimeSlots.map { "${it.start} - ${it.end}" },
                        onClick = {
                            uiEvent(RegisterUiEvent.UpdateTimePickerDialogVisibility(true))
                        },
                        onRemoveChipClick = {
                            val timeSlotString = it.split("-").map { it.trim() }
                            uiEvent(
                                RegisterUiEvent.OnTimeSlotRemoved(
                                    TimeSlot(
                                        start = timeSlotString.getOrNull(0),
                                        end = timeSlotString.getOrNull(1)
                                    )
                                )
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                expanded = uiState.isTimepickerDialogVisible
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

            }
        }


        Row(modifier = Modifier.clickable {
            uiEvent(RegisterUiEvent.OnTermsAndConditionsClick)
        }) {
            Checkbox(checked = uiState.isTermConditionAccepted,
                onCheckedChange = {
                    uiEvent(RegisterUiEvent.OnTermsAndConditionsClick)
                })
            Text(
                buildAnnotatedString {
                    val span = "Terms & Conditions"
                    val str =
                        "By proceeding with registration, you agree to our Terms & Conditions."
                    val startIndex = str.indexOf(span)
                    val endIndex = startIndex + span.length
                    append(str)
                    addStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        ), start = startIndex, end = endIndex
                    )
                }
            )
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
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
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


@Composable
private fun TextFieldChipHolder(
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
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialTheme.shapes.small
            )
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onClick() }
            .then(
                if (mSelectedValues.isEmpty()) Modifier.padding(16.dp, 8.dp)
                else Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            .heightIn(min = 40.dp)
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