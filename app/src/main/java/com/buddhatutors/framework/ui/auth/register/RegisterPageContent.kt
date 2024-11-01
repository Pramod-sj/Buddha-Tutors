@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.framework.ui.auth.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.User
import com.buddhatutors.framework.ActionIconButton
import com.buddhatutors.framework.ui.auth.register.common.AvailabilityCalendarChooser
import com.buddhatutors.framework.ui.common.Navigator
import com.buddhatutors.framework.ui.termconditions.EXTRA_IS_ACCEPTED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewRegisterPage() {
    RegisterPageContent(
        uiState = RegisterUiState(
            topics = listOf(Topic("", "Testing 1", true)),
        ),
        uiEvent = {},
        uiEffect = flow { }
    )
}

@Composable
internal fun RegisterPage() {

    val viewModel = hiltViewModel<RegisterViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    RegisterPageContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        uiEffect = viewModel.effect
    )
}


@Composable
internal fun RegisterPageContent(
    uiState: RegisterUiState,
    uiEvent: (RegisterUiEvent) -> Unit,
    uiEffect: Flow<RegisterUiEffect>
) {

    val navigator = Navigator

    // Retrieve the saved state handle from the previous back stack entry
    val savedStateHandle = navigator.currentBackStackEntry?.savedStateHandle

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    var showTopicDialog by remember { mutableStateOf(false) }

    val termConditionResultState by savedStateHandle
        ?.getStateFlow(EXTRA_IS_ACCEPTED, false)
        ?.collectAsState(false) ?: remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
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
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        uiEvent(RegisterUiEvent.OnRegisterClick)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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
                    shape = RoundedCornerShape(6.dp),
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
                    shape = RoundedCornerShape(6.dp),
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
                    shape = RoundedCornerShape(6.dp),
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
                    shape = RoundedCornerShape(6.dp),
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

                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = "How you want to register yourself?"
                    )

                    Row {
                        User.UserType.entries.forEach { text ->
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

                AnimatedVisibility(uiState.userType == User.UserType.TUTOR) {

                    Column(modifier = Modifier.fillMaxWidth()) {

                        if (uiState.topics.isNotEmpty()) {

                            Spacer(Modifier.height(4.dp))

                            Column(modifier = Modifier.wrapContentHeight()) {
                                Text(
                                    "Expertise in ",
                                    modifier = Modifier.clickable { showTopicDialog = true })
                                Spacer(modifier = Modifier.height(6.dp))
                                MultiSelectTextFieldDropdown(
                                    modifier = Modifier.fillMaxWidth(),
                                    selectedValues = uiState.selectedTopics.map { topic -> topic.label },
                                    options = uiState.topics.map { topic -> topic.label },
                                    label = "Select topics",
                                    onValueChangedEvent = { value ->
                                        uiEvent(RegisterUiEvent.OnExpertiseTopicChanged(value))
                                    }
                                )
                            }

                        }

                        Spacer(Modifier.height(16.dp))

                        AvailabilityCalendarChooser(
                            selectedDays = uiState.selectedAvailabilityDay,
                            selectedTimeSlot = uiState.selectedTimeSlot,
                            onDaysSelectionChangeEvent = { days ->
                                uiEvent(RegisterUiEvent.OnDayAvailabilityChanged(days))
                            },
                            onTimeSelectionChangeEvent = { timeSlot ->
                                uiEvent(RegisterUiEvent.OnTimeAvailabilityChanged(timeSlot))
                            }
                        )
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
    }

    LaunchedEffect(Unit) {
        uiEffect.collect { uiEffect ->
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
                    navigator.navigate("/termConditions")
                }
            }
        }
    }

    LaunchedEffect(termConditionResultState) {
        uiEvent(RegisterUiEvent.OnTermsAndConditionsStateChanged(termConditionResultState))
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )

        ExposedDropdownMenu(
            modifier = Modifier.height(200.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
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
            modifier = Modifier.heightIn(max = 200.dp),
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