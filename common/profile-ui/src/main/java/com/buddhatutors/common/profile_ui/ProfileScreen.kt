@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.buddhatutors.common.profile_ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.common.navigation.MasterTutorGraph
import com.buddhatutors.common.navigation.ProfileGraph
import com.buddhatutors.common.navigation.Splash
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.TutorGraph
import com.buddhatutors.domain.model.user.User
import com.buddhatutors.domain.model.user.UserType
import com.buddhatutors.domain.model.user.UserType.*
import com.buddhatutors.domain.model.user.toUserType

@Preview
@Composable
internal fun PreviewProfilePage() {
    ProfileScreenContent(uiState = ProfileUiState(
        User(
            id = "",
            name = "Pramod S",
            email = "pramosinghjantwal@gmail.com",
            userType = STUDENT
        )
    ), uiEvent = {})
}

@Composable
fun ProfileScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<ProfileViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenContent(uiState, viewModel::setEvent)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileUiEffect.NavigateToLoginPage -> {
                    navigator.navigate(
                        route = AuthGraph,
                        navOptions = navOptions {
                            when (effect.useType.toUserType()) {

                                STUDENT -> {
                                    popUpTo(StudentGraph) { inclusive = true }
                                }

                                TUTOR -> {
                                    popUpTo(TutorGraph) { inclusive = true }
                                }

                                ADMIN -> {
                                    popUpTo(AdminGraph) { inclusive = true }
                                }

                                MASTER_TUTOR -> {
                                    popUpTo(MasterTutorGraph) { inclusive = true }
                                }

                                else -> {
                                    popUpTo(StudentGraph) { inclusive = true }
                                    popUpTo(TutorGraph) { inclusive = true }
                                    popUpTo(AdminGraph) { inclusive = true }
                                    popUpTo(MasterTutorGraph) { inclusive = true }
                                }
                            }

                        }
                    )
                }

                is ProfileUiEffect.ShowMessage -> {

                }
            }
        }
    }
}

@Composable
internal fun ProfileScreenContent(
    uiState: ProfileUiState,
    uiEvent: (ProfileUiEvent) -> Unit
) {
    // Scaffold for structure (top bar, content, etc.)
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {},
                navigationIcon = {
                    ActionIconButton(
                        imageVector = Icons.Filled.ArrowBack,
                        iconTint = Color.Black
                    ) {
                        // navigator.popBackStack()
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top
            ) {

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Profile image placeholder image if userImageUrl is null
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                                .padding(16.dp),
                            tint = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // User's name
                        Text(
                            text = uiState.user?.name.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // User's email
                        Text(
                            text = uiState.user?.email.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))


                    }
                }

                item {

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { uiEvent(ProfileUiEvent.LogoutClick) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ExitToApp,
                                contentDescription = ""
                            )
                        },
                        headlineContent = { Text("Logout") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        },
                    )

                }

            }
        }
    )
}
