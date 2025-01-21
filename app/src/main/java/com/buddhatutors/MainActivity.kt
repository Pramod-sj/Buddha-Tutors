package com.buddhatutors

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.buddhatutors.common.BuddhaTutorsProvider
import com.buddhatutors.common.collectAsEffect
import com.buddhatutors.common.messaging.Message
import com.buddhatutors.common.messaging.MessageComposable
import com.buddhatutors.common.messaging.MessageHelper
import com.buddhatutors.common.theme.BuddhaTutorTheme
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.domain.CurrentUser
import com.buddhatutors.feature.admin.add_master_tutor.navigation.navigateToAddMasterTutor
import com.buddhatutors.feature.admin.add_master_tutor.navigation.registerAddMasterTutor
import com.buddhatutors.feature.admin.add_topic.navigation.navigateToAddTopic
import com.buddhatutors.feature.admin.add_topic.navigation.registerAddTopic
import com.buddhatutors.feature.admin.add_tutor.navigation.navigateToAddTutor
import com.buddhatutors.feature.admin.add_tutor.navigation.registerAddTutor
import com.buddhatutors.feature.admin.home.navigation.navigateToAdminMainPage
import com.buddhatutors.feature.admin.home.navigation.registerAdminMainScreen
import com.buddhatutors.feature.admin.master_home.navigation.navigateToMasterTutorHomePage
import com.buddhatutors.feature.admin.master_home.navigation.registerMasterTutorHomeScreen
import com.buddhatutors.feature.admin.tutor_detail_verification.navigation.navigateToAdminTutorVerificationScreen
import com.buddhatutors.feature.admin.tutor_detail_verification.navigation.registerAdminTutorVerification
import com.buddhatutors.feature.forgotpassword.navigation.navigateToForgotPasswordDialog
import com.buddhatutors.feature.forgotpassword.navigation.registerForgotPasswordDialog
import com.buddhatutors.feature.login.navigation.ExternalLoginNavigationHandler
import com.buddhatutors.feature.login.navigation.navigateToLoginScreen
import com.buddhatutors.feature.login.navigation.registerLoginScreen
import com.buddhatutors.feature.registration.navigation.navigateToUserRegistration
import com.buddhatutors.feature.registration.navigation.registerUserRegistrationScreen
import com.buddhatutors.feature.student.home.navigation.navigateToStudentMainPage
import com.buddhatutors.feature.student.home.navigation.registerStudentMainPage
import com.buddhatutors.feature.student.slot_booking.navigation.navigateToStudentTutorDetailPage
import com.buddhatutors.feature.student.slot_booking.navigation.registerStudentTutorDetailPage
import com.buddhatutors.feature.termconditions.navigation.navigateToTermConditionScreen
import com.buddhatutors.feature.termconditions.navigation.registerTermConditionScreen
import com.buddhatutors.feature.tutor.edit_tutor.navigation.navigateToEditTutorAvailabilityPage
import com.buddhatutors.feature.tutor.edit_tutor.navigation.registerEditTutorAvailabilityPage
import com.buddhatutors.feature.tutor.home.navigation.navigateToTutorMainPage
import com.buddhatutors.feature.tutor.home.navigation.registerTutorMainPage
import com.buddhatutors.feature.userprofile.navigation.navigateToUserProfileScreen
import com.buddhatutors.feature.userprofile.navigation.registerUserProfileScreen
import com.buddhatutors.ui.splash.navigation.SplashRoute
import com.buddhatutors.ui.splash.navigation.registerSplash
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionDataSource: UserSessionPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
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

            var message by remember { mutableStateOf<Message?>(null) }

            MessageHelper.message.collectAsEffect {
                message = it
            }

            val loginNavigationHandler = remember {
                DefaultLoginNavigationHandler(
                    navController
                )
            }

            BuddhaTutorsProvider(navHost = navController) {

                BuddhaTutorTheme {

                    Box(modifier = Modifier.fillMaxSize()) {

                        NavHost(
                            navController = navController,
                            startDestination = SplashRoute
                        ) {

                            registerSplash(
                                openLoginPage = {
                                    navController.navigateToLoginScreen(navOptions {
                                        navController.currentDestination?.route?.let { currRoute ->
                                            popUpTo(currRoute) { inclusive = true }
                                        }
                                    })
                                },
                                openStudentHomePage = {
                                    navController.navigateToStudentMainPage(navOptions {
                                        navController.currentDestination?.route?.let { currRoute ->
                                            popUpTo(currRoute) { inclusive = true }
                                        }
                                    })
                                },
                                openTutorHomePage = {
                                    navController.navigateToTutorMainPage(navOptions {
                                        navController.currentDestination?.route?.let { currRoute ->
                                            popUpTo(currRoute) { inclusive = true }
                                        }
                                    })
                                },
                                openAdminHomePage = {
                                    navController.navigateToAdminMainPage(navOptions {
                                        navController.currentDestination?.route?.let { currRoute ->
                                            popUpTo(currRoute) { inclusive = true }
                                        }
                                    })
                                },
                                openMasterTutorHomePage = {
                                    navController.navigateToMasterTutorHomePage(navOptions {
                                        navController.currentDestination?.route?.let { currRoute ->
                                            popUpTo(currRoute) { inclusive = true }
                                        }
                                    })
                                }
                            )

                            //region auth pages

                            registerLoginScreen(loginNavigationHandler = loginNavigationHandler)

                            registerUserRegistrationScreen(
                                navigateToTermCondition = navController::navigateToTermConditionScreen
                            )

                            registerForgotPasswordDialog()

                            registerTermConditionScreen()

                            //endregion

                            registerUserProfileScreen(
                                openLoginPage = {
                                    navController.navigateToLoginScreen(
                                        navOptions = navOptions {
                                            navController.currentDestination?.route?.let { currRoute ->
                                                popUpTo(currRoute) { inclusive = true }
                                            }
                                        }
                                    )
                                },
                                openEditTutorPage = { tutorId ->
                                    navController.navigateToEditTutorAvailabilityPage(tutorId)
                                }
                            )


                            //region admin pages

                            registerAdminMainScreen(
                                openAddTutorPage = {
                                    navController.navigateToAddTutor()
                                },
                                openTutorVerificationPage = { tutorListing ->
                                    navController.navigateToAdminTutorVerificationScreen(
                                        tutorListing
                                    )
                                },
                                openUserProfilePage = {
                                    navController.navigateToUserProfileScreen()
                                },
                                openAddMasterTutorPage = {
                                    navController.navigateToAddMasterTutor()
                                },
                                openAddTopicPage = {
                                    navController.navigateToAddTopic()
                                }
                            )

                            registerAddTutor()

                            registerAddMasterTutor()

                            registerAddTopic()

                            registerAdminTutorVerification()

                            //endregion

                            //region master tutor pages

                            registerMasterTutorHomeScreen(
                                navigateToTutorVerificationScreen = {},
                                navigateToUserProfileScreen = {
                                    navController.navigateToUserProfileScreen()
                                },
                                navigateToAddTutorScreen = {
                                    navController.navigateToAddTutor()
                                }
                            )


                            //endregion

                            //region Tutor pages

                            registerTutorMainPage(
                                openBookedSlotDetail = { bookedSlot ->

                                },
                                openUserProfileScreen = {
                                    navController.navigateToUserProfileScreen()
                                }
                            )

                            registerEditTutorAvailabilityPage()

                            //endregion

                            //region Student pages

                            registerStudentMainPage(
                                openTutorDetailScreen = {
                                    navController.navigateToStudentTutorDetailPage(it)
                                },
                                openUserProfileScreen = {
                                    navController.navigateToUserProfileScreen()
                                }
                            )

                            registerStudentTutorDetailPage()

                            //endregion

                            /*
                            navigation<AuthGraph>(startDestination = AuthGraph.LoginUser) {

                                navComposable<AuthGraph.LoginUser> { com.buddhatutors.feature.login.LoginScreen() }

                                navDialogComposable<AuthGraph.ForgotPassword> { com.buddhatutors.feature.login.forgotpassword.ForgotPasswordScreen() }

                                navComposable<AuthGraph.RegisterUser> { RegisterScreen() }

                                navComposable<AuthGraph.TermAndConditions> { com.buddhatutors.feature.termconditions.TermConditionScreen() }


                            }

                            navigation<AdminGraph>(startDestination = AdminGraph.Home) {

                                navComposable<AdminGraph.Home> { AdminMainScreen() }

                                navComposable<AdminGraph.AddTutor> { AddTutorUserScreen() }

                                navComposable<AdminGraph.AddMasterTutorUser> { AddMasterTutorUserScreen() }

                                navComposable<AdminGraph.AdminTutorVerification>(
                                    typeMap = mapOf(navigationCustomArgument<com.buddhatutors.model.tutorlisting.TutorListing>())
                                ) { TutorVerificationScreen() }

                                navDialogComposable<AdminGraph.AddTopic> { AddTopicScreen() }

                            }

                            navigation<MasterTutorGraph>(startDestination = MasterTutorGraph.MasterTutorHome) {

                                navComposable<MasterTutorGraph.MasterTutorHome> { MasterTutorHomeScreen() }

                                navComposable<MasterTutorGraph.AddMasterTutorUser> { AddTutorUserScreen() }

                                navComposable<MasterTutorGraph.AdminTutorVerification>(
                                    typeMap = mapOf(navigationCustomArgument<com.buddhatutors.model.tutorlisting.TutorListing>())
                                ) { TutorVerificationScreen() }

                            }

                            navigation<StudentGraph>(startDestination = StudentGraph.Main) {

                                navComposable<StudentGraph.Main> { StudentMainPage() }

                                navComposable<StudentGraph.TutorDetail>(
                                    typeMap = mapOf(navigationCustomArgument<com.buddhatutors.model.tutorlisting.TutorListing>())
                                ) { TutorDetailScreen() }

                            }

                            navigation<TutorGraph>(startDestination = TutorGraph.Home) {

                                navComposable<TutorGraph.Home> { TutorHomeScreen() }

                                navComposable<TutorGraph.EditTutorAvailability>(
                                    typeMap = mapOf(navigationCustomArgument<String>())
                                ) { EditTutorScreen() }

                            }

                            navigation<ProfileGraph>(startDestination = ProfileGraph.Home) {

                                navComposable<ProfileGraph.Home> { ProfileScreen() }

                            }
*/

                        }

                        message?.let { messageData ->
                            MessageComposable(
                                message = messageData,
                                onDismiss = {
                                    message = null
                                }
                            )
                        }

                    }
                }
            }


        }
    }

    override fun onDestroy() {
        CurrentUser.dispose()
        super.onDestroy()
    }
}


private class DefaultLoginNavigationHandler(
    private val navNavHostController: NavHostController
) : ExternalLoginNavigationHandler {

    override fun navigateToTermsAndConditions() {
        navNavHostController.navigateToTermConditionScreen()
    }

    override fun navigateToRegistration() {
        navNavHostController.navigateToUserRegistration()
    }

    override fun navigateToAdminHome() {
        navNavHostController.navigateToAdminMainPage()
    }

    override fun navigateToStudentHome() {
        navNavHostController.navigateToStudentMainPage()
    }

    override fun navigateToTutorHome() {
        navNavHostController.navigateToTutorMainPage()
    }

    override fun navigateToMasterTutor() {
        navNavHostController.navigateToMasterTutorHomePage()
    }

    override fun openForgotPassword() {
        navNavHostController.navigateToForgotPasswordDialog()
    }
}
