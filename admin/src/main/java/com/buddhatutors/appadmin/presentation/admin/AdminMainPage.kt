package com.buddhatutors.appadmin.presentation.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buddhatutors.appadmin.presentation.admin.home.AdminHomeScreen
import com.buddhatutors.appadmin.presentation.admin.topics.viewtopics.ManageTopicScreen
import com.buddhatutors.appadmin.presentation.admin.viewmastertutor.ViewMasterTutorListScreen

@Composable
fun AdminMainScreen() {

    val navController = rememberNavController()

    val bottomMenuList = remember {
        listOf(
            AdminBottomNavigationItems.Home,
            AdminBottomNavigationItems.ManageMasterTutor,
            AdminBottomNavigationItems.ManageTopic
        )
    }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomMenuList.forEach { menuItem ->
                    val isSelected = currentRoute == menuItem.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) menuItem.selectedIcon else menuItem.unSelectedIcon,
                                contentDescription = menuItem.label
                            )
                        },
                        label = { Text(menuItem.label) },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != menuItem.route) {
                                navController.navigate(menuItem.route)
                            }
                        }
                    )
                }
            }
        }
    ) {

        Box(Modifier.padding(bottom = it.calculateBottomPadding())) {

            NavHost(
                navController = navController,
                startDestination = AdminBottomNavigationItems.Home.route
            ) {

                composable(AdminBottomNavigationItems.Home.route) {
                    AdminHomeScreen()
                }

                composable(AdminBottomNavigationItems.ManageMasterTutor.route) {
                    ViewMasterTutorListScreen()
                }

                composable(AdminBottomNavigationItems.ManageTopic.route) {
                    ManageTopicScreen()
                }

            }

        }
    }

}


sealed class AdminBottomNavigationItems(
    val route: String,
    val label: String,
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home :
        AdminBottomNavigationItems(
            route = "/admin/home",
            label = "Home",
            unSelectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        )

    data object ManageMasterTutor :
        AdminBottomNavigationItems(
            route = "/admin/master_tutor",
            label = "Master tutors",
            unSelectedIcon = Icons.Outlined.Shield,
            selectedIcon = Icons.Filled.Shield
        )

    data object ManageTopic :
        AdminBottomNavigationItems(
            route = "/admin/topic",
            label = "Topic",
            unSelectedIcon = Icons.Outlined.Book,
            selectedIcon = Icons.Filled.Book
        )

}