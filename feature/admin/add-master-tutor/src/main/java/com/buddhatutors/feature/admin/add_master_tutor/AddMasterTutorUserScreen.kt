@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)

package com.buddhatutors.feature.admin.add_master_tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
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
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.DefaultBuddhaTutorTextFieldColors
import com.buddhatutors.common.FullScreenLoader
import com.buddhatutors.common.Navigator
import com.buddhatutors.core.constant.ScreenResultConstant.EXTRA_TUTOR_CHANGED_RESULT
import kotlinx.coroutines.launch

@Preview
@Composable
internal fun PreviewAddTutorUserScreen() {
    AddMasterTutorUserScreenContent(
        modifier = Modifier.background(Color.White),
        uiState = AddUserUiState(),
        uiEvent = {}
    )
}

@Composable
fun AddMasterTutorUserScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<AddMasterTutorViewModel>()

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
                            text = "Add master tutor"
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
                    enabled = uiState.isAddButtonEnabled
                ) {
                    Text(text = "Save master tutor")
                }
            }
        }
    ) {

        AddMasterTutorUserScreenContent(
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
                        ?.set(EXTRA_TUTOR_CHANGED_RESULT, true)

                    navigator.popBackStack()
                }
            }
        }
    }

    FullScreenLoader(uiState.showFullScreenLoader)
}


@Composable
internal fun AddMasterTutorUserScreenContent(
    modifier: Modifier,
    uiState: AddUserUiState,
    uiEvent: (AddUserUiEvent) -> Unit
) {

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