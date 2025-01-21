package com.buddhatutors.feature.admin.add_topic.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.buddhatutors.common.navDialogComposable
import com.buddhatutors.feature.admin.add_topic.AddTopicScreen
import kotlinx.serialization.Serializable


@Serializable
object AddTopicRoute

fun NavController.navigateToAddTopic(navOptions: NavOptions? = null) =
    navigate(route = AddTopicRoute, navOptions)

fun NavGraphBuilder.registerAddTopic() {
    navDialogComposable<AddTopicRoute> { AddTopicScreen() }
}