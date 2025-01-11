package com.buddhatutors

import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.buddhatutors.appadmin.presentation.admin.AdminMainScreen
import com.buddhatutors.appadmin.presentation.admin.addmastertutor.AddMasterTutorUserScreen
import com.buddhatutors.appadmin.presentation.admin.topics.addtopics.AddTopicScreen
import com.buddhatutors.appadmin.presentation.common.tutorverification.TutorVerificationScreen
import com.buddhatutors.appadmin.presentation.common.addtutor.AddTutorUserScreen
import com.buddhatutors.appadmin.presentation.master_tutor.home.MasterTutorHomeScreen
import com.buddhatutors.auth.domain.datasource.UserSessionDataSource
import com.buddhatutors.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.buddhatutors.auth.presentation.login.LoginScreen
import com.buddhatutors.auth.presentation.register.RegisterScreen
import com.buddhatutors.auth.presentation.termconditions.TermConditionScreen
import com.buddhatutors.common.BuddhaTutorsProvider
import com.buddhatutors.common.domain.CurrentUser
import com.buddhatutors.common.domain.model.tutorlisting.TutorListing
import com.buddhatutors.common.navComposable
import com.buddhatutors.common.navDialogComposable
import com.buddhatutors.common.navigation.AdminGraph
import com.buddhatutors.common.navigation.AuthGraph
import com.buddhatutors.common.navigation.MasterTutorGraph
import com.buddhatutors.common.navigation.ProfileGraph
import com.buddhatutors.common.navigation.Splash
import com.buddhatutors.common.navigation.StudentGraph
import com.buddhatutors.common.navigation.TutorGraph
import com.buddhatutors.common.navigation.navigationCustomArgument
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.ui.splash.SplashScreen
import com.buddhatutors.user.presentation.student.main.StudentMainPage
import com.buddhatutors.user.presentation.student.tutorslotbooking.TutorDetailScreen
import com.buddhatutors.userprofile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionDataSource: UserSessionDataSource

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        val fakeEvent = MotionEvent.obtain(
            event.downTime,        // Down time of the event (start time of the first touch)
            event.eventTime,       // Event time of the touch event
            MotionEvent.ACTION_DOWN, // Action type (ACTION_DOWN for the start of the touch)
            event.x,               // X position of touch (location where touch happens)
            event.y,               // Y position of touch (location where touch happens)
            1f,                    // Pressure (higher for stylus, 1f for full pressure)
            1f,                    // Size (stylus is more precise, so set size to 1f or smaller)
            event.metaState,       // Meta state (keyboard state, shift, etc.)
            event.xPrecision,      // xPrecision: The precision of the touch in the X direction
            event.yPrecision,      // yPrecision: The precision of the touch in the Y direction
            event.deviceId,        // Device ID (usually from the original touch event)
            event.edgeFlags        // Edge flags (e.g., if touch occurs at the edge)
        )

        return super.dispatchTouchEvent(ev)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                userSessionDataSource.getUserSession().collect { user ->
                    CurrentUser.setUser(user)
                }
            }
        }

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

                            navDialogComposable<AuthGraph.ForgotPassword> { ForgotPasswordScreen() }

                            navComposable<AuthGraph.RegisterUser> { RegisterScreen() }

                            navComposable<AuthGraph.TermAndConditions> { TermConditionScreen() }


                        }


                        navigation<AdminGraph>(startDestination = AdminGraph.Home) {

                            navComposable<AdminGraph.Home> { AdminMainScreen() }

                            navComposable<AdminGraph.AddTutor> { AddTutorUserScreen() }

                            navComposable<AdminGraph.AddMasterTutorUser> { AddMasterTutorUserScreen() }

                            navComposable<AdminGraph.AdminTutorVerification>(
                                typeMap = mapOf(navigationCustomArgument<TutorListing>())
                            ) { TutorVerificationScreen() }

                            navDialogComposable<AdminGraph.AddTopic> { AddTopicScreen() }

                        }

                        navigation<MasterTutorGraph>(startDestination = MasterTutorGraph.MasterTutorHome) {

                            navComposable<MasterTutorGraph.MasterTutorHome> { MasterTutorHomeScreen() }

                            navComposable<MasterTutorGraph.AddMasterTutorUser> { AddTutorUserScreen() }

                            navComposable<MasterTutorGraph.AdminTutorVerification>(
                                typeMap = mapOf(navigationCustomArgument<TutorListing>())
                            ) { TutorVerificationScreen() }

                        }


                        navigation<StudentGraph>(startDestination = StudentGraph.Main) {

                            navComposable<StudentGraph.Main> { StudentMainPage() }

                            navComposable<StudentGraph.TutorDetail>(
                                typeMap = mapOf(navigationCustomArgument<TutorListing>())
                            ) { TutorDetailScreen() }

                        }

                        navigation<TutorGraph>(startDestination = TutorGraph.Home) {

                            navComposable<TutorGraph.Home> { com.buddhatutors.user.presentation.tutor.home.TutorHomeScreen() }

                        }

                        navigation<ProfileGraph>(startDestination = ProfileGraph.Home) {

                            navComposable<ProfileGraph.Home> { ProfileScreen() }

                        }

                    }
                }
            }


        }
    }

    override fun onDestroy() {
        com.buddhatutors.common.domain.CurrentUser.dispose()
        super.onDestroy()
    }
}