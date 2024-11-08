@file:OptIn(ExperimentalMaterial3Api::class)

package com.buddhatutors.common.auth.ui.termconditions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.Navigator

@Preview
@Composable
fun PreviewTermConditionPageContent() {
    TermConditionScreen()
}

const val EXTRA_IS_ACCEPTED = "extra_is_accepted"

@Composable
fun TermConditionScreen() {

    val navigator = Navigator

    val viewModel = hiltViewModel<TermConditionViewModel>()

    val termConditions by viewModel.termCondition.collectAsState(emptyList())

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "To continue with your registration, please review and agree to our Terms & Conditions.",
                        style = MaterialTheme.typography.labelMedium
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
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        navigator.previousBackStackEntry?.savedStateHandle?.set(
                            EXTRA_IS_ACCEPTED,
                            false
                        )
                        navigator.popBackStack()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(text = "Decline")
                }

                Button(
                    modifier = Modifier.weight(0.5f),
                    onClick = {
                        navigator.previousBackStackEntry?.savedStateHandle?.set(
                            EXTRA_IS_ACCEPTED,
                            true
                        )
                        navigator.popBackStack()
                    }) {
                    Text(text = "Agree")
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clipToBounds(),
            contentPadding = it
        ) {

            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        modifier = Modifier,
                        text = "Last updated at 10th Oct'24",
                        style = MaterialTheme.typography.labelSmall
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            itemsIndexed(termConditions) { pos, termCondition ->

                Text(
                    text = "${(pos + 1)}. ${termCondition.title}",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = termCondition.desc,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }

    LaunchedEffect(Unit) { viewModel.loadTermConditions() }
}