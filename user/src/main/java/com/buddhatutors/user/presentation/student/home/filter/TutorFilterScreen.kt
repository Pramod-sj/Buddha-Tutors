@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.buddhatutors.user.presentation.student.home.filter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.buddhatutors.common.ActionIconButton
import com.buddhatutors.common.domain.model.FilterOption
import java.util.UUID

@Preview
@Composable
internal fun PreviewTutorFilterScreen() {
    TutorFilterScreen(null, {}, {})
}

@Composable
fun TutorFilterScreen(
    filterOption: FilterOption?,
    onCloseClick: () -> Unit,
    onFilterApplied: (filterOption: FilterOption?) -> Unit,
) {

    val onFilterAppliedUpdatedState by rememberUpdatedState(onFilterApplied)

    val tutorFilterViewModel =
        hiltViewModel<TutorFilterViewModel>(key = UUID.randomUUID().toString())

    LaunchedEffect(filterOption) {
        tutorFilterViewModel.setEvent(TutorFilterUiEvent.SetDefaultFilters(filterOption))
    }

    val uiState by tutorFilterViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Filters")
                },
                actions = {
                    ActionIconButton(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Close,
                        iconTint = Color.Black
                    ) {
                        onCloseClick()
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { tutorFilterViewModel.setEvent(TutorFilterUiEvent.ClearFilter) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { tutorFilterViewModel.setEvent(TutorFilterUiEvent.ApplyFilter) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {

            item {
                Text("Select Topics", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    uiState.topics.forEach { topic ->
                        FilterChip(
                            selected = uiState.filterOption?.topics?.contains(topic) == true,
                            onClick = {
                                if (uiState.filterOption?.topics?.contains(topic) == true) {
                                    tutorFilterViewModel.setEvent(
                                        TutorFilterUiEvent.RemoveTopicFilter(
                                            topic
                                        )
                                    )
                                } else {
                                    tutorFilterViewModel.setEvent(
                                        TutorFilterUiEvent.AddTopicFilter(
                                            topic
                                        )
                                    )
                                }
                            },
                            label = { Text(topic.label) },
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Select Languages", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    uiState.languages.forEach { language ->
                        FilterChip(
                            selected = uiState.filterOption?.languages?.contains(language) == true,
                            onClick = {
                                if (uiState.filterOption?.languages?.contains(language) == true) {
                                    tutorFilterViewModel.setEvent(
                                        TutorFilterUiEvent.RemoveLanguageFilter(
                                            language
                                        )
                                    )
                                } else {
                                    tutorFilterViewModel.setEvent(
                                        TutorFilterUiEvent.AddLanguageFilter(language)
                                    )
                                }
                            },
                            label = { Text(language) },
                            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        tutorFilterViewModel.effect.collect {
            when (it) {
                is TutorFilterUiEffect.FilterApplied -> {
                    onFilterAppliedUpdatedState(it.filterOption)
                }
            }
        }
    }

}