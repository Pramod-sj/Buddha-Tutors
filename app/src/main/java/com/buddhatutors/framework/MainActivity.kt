package com.buddhatutors.framework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.buddhatutors.framework.ui.SplashPage
import com.buddhatutors.framework.ui.auth.LoginPage
import com.buddhatutors.framework.ui.auth.register.RegisterPage
import com.buddhatutors.framework.ui.common.BuddhaTutorsProvider
import com.buddhatutors.framework.ui.common.navComposable
import com.buddhatutors.framework.ui.home.HomePage
import com.buddhatutors.framework.ui.termconditions.TermConditionPage
import com.buddhatutors.framework.ui.theme.BuddhaTutorTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
        )


        setContent {

            val navController = rememberNavController()

            BuddhaTutorsProvider(navHost = navController) {
                BuddhaTutorTheme {
                    NavHost(
                        navController = navController,
                        startDestination = "/splash"
                    ) {
                        navComposable("/splash") { SplashPage() }
                        navComposable("/login") { LoginPage() }
                        navComposable("/register") { RegisterPage() }
                        navComposable("/home") { HomePage() }
                        navComposable("/termConditions") { TermConditionPage() }
                    }
                }
            }


        }
    }
}