package com.buddhatutors.user.presentation.student.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buddhatutors.user.presentation.student.home.StudentHomeScreen
import com.buddhatutors.user.presentation.student.session.MeetingSessionScreen

@Composable
fun StudentMainPage() {

    val navController = rememberNavController()

    val bottomMenuList =
        listOf(StudentBottomNavigationItems.Home, StudentBottomNavigationItems.MeetingSessions)

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
                startDestination = StudentBottomNavigationItems.Home.route
            ) {

                composable(StudentBottomNavigationItems.Home.route) {
                    StudentHomeScreen()
                }

                composable(StudentBottomNavigationItems.MeetingSessions.route) {
                    MeetingSessionScreen()
                }

            }

        }
    }

}


sealed class StudentBottomNavigationItems(
    val route: String,
    val label: String,
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home :
        StudentBottomNavigationItems(
            route = "/student/home",
            label = "Home",
            unSelectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        )

    data object MeetingSessions :
        StudentBottomNavigationItems(
            route = "/student/session",
            label = "Sessions",
            unSelectedIcon = Icons.Outlined.MeetingRoom,
            selectedIcon = Icons.Filled.MeetingRoom
        )

}