@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.buddhatutors.feature.admin.tutor_detail_verification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.GenericBottomSheet
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.collectAsEffect
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.core.constant.ScreenResultConstant.EXTRA_TUTOR_CHANGED_RESULT
import com.buddhatutors.model.TimeSlot
import com.buddhatutors.model.tutorlisting.TutorListing
import com.buddhatutors.model.user.User
import com.buddhatutors.model.user.UserType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewTutorDetailPage() {
    BuddhaTutorTheme {
        TutorVerificationScreenContent(
            uiState = TutorVerificationUiState(
                tutorListing = TutorListing(
                    tutorUser = User(
                        id = "",
                        name = "",
                        email = "",
                        userType = UserType.STUDENT
                    ),
                    expertiseIn = listOf(),
                    availableDays = listOf("Monday", "Tuesday", "Wednesday"),
                    availableTimeSlots = listOf(
                        TimeSlot(
                            "08:00 AM",
                            "08:30 AM"
                        ),
                        TimeSlot(
                            "08:30 AM",
                            "09:00 AM"
                        ),
                        TimeSlot(
                            "09:00 AM",
                            "09:30 AM"
                        ),
                        TimeSlot(
                            "09:30 AM",
                            "10:00 AM"
                        )
                    ),
                    verification = null,
                )
            ),
            uiEvent = {},
            uiEffect = flow {  })
    }
}

@Composable
fun TutorVerificationScreen() {

    val viewModel = hiltViewModel<TutorVerificationViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TutorVerificationScreenContent(
        uiState = uiState,
        uiEvent = viewModel::setEvent,
        uiEffect = viewModel.effect
    )

}

@Composable
internal fun TutorVerificationScreenContent(
    uiState: TutorVerificationUiState,
    uiEvent: (TutorVerificationUiEvent) -> Unit,
    uiEffect: Flow<TutorVerificationUiEffect>
) {

    val navigator = Navigator

    val topAppBarBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val snackBarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    var warningBottomSheetEffectDataHolder by remember {
        mutableStateOf<TutorVerificationUiEffect?>(null)
    }


    BackHandler {

        if (uiState.isTutorDataChanged) {
            navigator.previousBackStackEntry
                ?.savedStateHandle
                ?.set(EXTRA_TUTOR_CHANGED_RESULT, true)
        }

        navigator.popBackStack()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarBehaviour.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = topAppBarBehaviour,
                title = {
                    Text(text = uiState.tutorListing?.tutorUser?.name ?: "")
                }, navigationIcon = {
                    ActionIconButton(
                        imageVector = Icons.Filled.ArrowBack, iconTint = Color.Black
                    ) {

                        if (uiState.isTutorDataChanged) {
                            navigator.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(EXTRA_TUTOR_CHANGED_RESULT, true)
                        }

                        navigator.popBackStack()
                    }
                })
        },
        bottomBar = {
            if (uiState.userType == UserType.ADMIN) {
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
                            onClick = { uiEvent(TutorVerificationUiEvent.RejectTutorButtonClick) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.errorContainer.copy(
                                    0.4f
                                ),
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
                            onClick = { uiEvent(TutorVerificationUiEvent.ApproveTutorButtonClick) }) {
                            Text(text = "Approve")
                        }

                    }

                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Speaks in ${uiState.tutorListing?.languages?.joinToString { it }}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Expertise in",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            uiState.tutorListing?.expertiseIn?.forEach {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                ) {
                    Text(text = "•", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(4.dp))
                    Text(text = it.label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))


            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Tutor availability",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Days :-", style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.tutorListing?.availableDays?.forEach {

                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Hours :-", style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            val itemSize: Dp = ((LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 2f)
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.tutorListing?.availableTimeSlots?.forEach { item ->
                    Text(
                        text = String.format("%s - %s", item.start, item.end),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .width(itemSize)
                            .wrapContentHeight()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                MaterialTheme.shapes.small
                            )
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            uiState.tutorListing?.addedByUser?.let { addedByUser ->
                UserInformationCard(user = addedByUser)
            }

        }
    }

    uiEffect.collectAsEffect {
        when (it) {
            is TutorVerificationUiEffect.ShowApprovalWarningPopup -> {
                warningBottomSheetEffectDataHolder = it
            }

            is TutorVerificationUiEffect.ShowRejectionWarningPopup -> {
                warningBottomSheetEffectDataHolder = it
            }

            is TutorVerificationUiEffect.ShowMessage -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }


    warningBottomSheetEffectDataHolder?.let { dataHolder ->
        when (dataHolder) {
            is TutorVerificationUiEffect.ShowApprovalWarningPopup -> {
                GenericBottomSheet(
                    title = dataHolder.title,
                    description = dataHolder.desc,
                    buttonText = dataHolder.buttonLabel,
                    onButtonClick = { dataHolder.buttonClickHandler.invoke() },
                    onDismissRequest = {
                        warningBottomSheetEffectDataHolder = null
                    }
                )
            }

            is TutorVerificationUiEffect.ShowRejectionWarningPopup -> {
                GenericBottomSheet(
                    title = dataHolder.title,
                    description = dataHolder.desc,
                    buttonText = dataHolder.buttonLabel,
                    onButtonClick = { dataHolder.buttonClickHandler.invoke() },
                    onDismissRequest = {
                        warningBottomSheetEffectDataHolder = null
                    }
                )
            }

            else -> Unit
        }
    }

}


@Composable
fun UserInformationCard(
    user: User,
    onCardClick: (() -> Unit)? = null // Optional callback for interactivity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(
                onCardClick?.let {
                    Modifier.clickable { onCardClick.invoke() }
                } ?: Modifier
            ), // Make the card clickable
        shape = MaterialTheme.shapes.large, // Rounded corners
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header with bold styling
            Text(
                text = "Tutor added by",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display user info with icons for clarity
            InfoRow(icon = Icons.Default.Person, label = "Name", value = user.name)
            InfoRow(icon = Icons.Default.Email, label = "Email", value = user.email)
            InfoRow(icon = Icons.Default.Shield, label = "Admin Type", value = user.userType.value)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$label Icon",
            modifier = Modifier
                .padding(end = 16.dp)
                .size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserInformationCard() {
    UserInformationCard(
        user = User(id = "", name = "", email = "", userType = UserType.STUDENT),
        onCardClick = { /* Handle card click, e.g., navigate to details screen */ }
    )
}
