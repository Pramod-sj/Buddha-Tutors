package com.buddhatutors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.buddhatutors.appadmin.home.AdminHomeScreen
import com.buddhatutors.appadmin.tutorverification.TutorVerificationScreen
import com.buddhatutors.common.BuddhaTutorsProvider
import com.buddhatutors.common.auth.ui.login.LoginScreen
import com.buddhatutors.common.auth.ui.register.RegisterScreen
import com.buddhatutors.common.auth.ui.termconditions.TermConditionScreen
import com.buddhatutors.common.navComposable
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.common.navigation.Splash
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.navigationCustomArgument
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.tutorlisting.TutorListing
import com.buddhatutors.student.home.StudentHomeScreen
import com.buddhatutors.student.tutorslotbooking.TutorDetailScreen
import com.buddhatutors.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionDataSource: UserSessionDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
        )

        CurrentUser.initialize(userSessionDataSource)

        setContent {

            val navController = rememberNavController()

            BuddhaTutorsProvider(navHost = navController) {

                BuddhaTutorTheme {

                    NavHost(
                        navController = navController,
                        startDestination = Splash
                    ) {

                        navComposable<Splash> { SplashScreen() }

                        navigation<AuthGraph>(startDestination = AuthGraph.LoginUser) {

                            navComposable<AuthGraph.LoginUser> { LoginScreen() }

                            navComposable<AuthGraph.RegisterUser> { RegisterScreen() }

                            navComposable<AuthGraph.TermAndConditions> { TermConditionScreen() }

                        }


                        navigation<AdminGraph>(startDestination = AdminGraph.Home) {

                            navComposable<AdminGraph.Home> { AdminHomeScreen() }

                            navComposable<AdminGraph.AdminTutorVerification>(
                                typeMap = mapOf(navigationCustomArgument<TutorListing>())
                            ) { TutorVerificationScreen() }

                        }


                        navigation<StudentGraph>(startDestination = StudentGraph.Home) {

                            navComposable<StudentGraph.Home> { StudentHomeScreen() }

                            navComposable<StudentGraph.TutorDetail>(
                                typeMap = mapOf(navigationCustomArgument<TutorListing>())
                            ) { TutorDetailScreen() }

                        }

                    }
                }
            }


        }
    }
}