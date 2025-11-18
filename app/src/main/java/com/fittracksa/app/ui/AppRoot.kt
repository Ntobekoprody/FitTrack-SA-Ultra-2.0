package com.fittracksa.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.notifications.FitTrackNotifier
import com.fittracksa.app.ui.screens.activity.ActivityScreen
import com.fittracksa.app.ui.screens.achievements.AchievementsScreen
import com.fittracksa.app.ui.screens.dashboard.DashboardScreen
import com.fittracksa.app.ui.screens.login.LoginScreen
import com.fittracksa.app.ui.screens.login.RegisterScreen
import com.fittracksa.app.ui.screens.nutrition.NutritionScreen
import com.fittracksa.app.ui.screens.progress.ProgressScreen
import com.fittracksa.app.ui.screens.settings.SettingsScreen
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.White
import com.fittracksa.app.ui.theme.Lime

//--------------------------------------
// Navigation Destinations
//--------------------------------------
sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Register : Destination("register")
    data object Dashboard : Destination("dashboard")
    data object Activity : Destination("activity")
    data object Nutrition : Destination("nutrition")
    data object Progress : Destination("progress")
    data object Settings : Destination("settings")
    data object Achievements : Destination("achievements")
}

//--------------------------------------
// App Root
//--------------------------------------
@Composable
fun AppRoot(
    dataViewModel: SharedDataViewModel,
    settingsViewModel: SettingsViewModel,
    userSettings: UserSettings,
    notifier: FitTrackNotifier
) {
    val navController = rememberNavController()
    val strings = stringsFor(userSettings.language)
    val isDark = userSettings.isDarkMode

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            if (currentRoute != Destination.Login.route && currentRoute != Destination.Register.route) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isDark = isDark,
                    strings = strings
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Black else White)
                .padding(paddingValues)
        ) {
            AppNavHost(
                navController = navController,
                dataViewModel = dataViewModel,
                settingsViewModel = settingsViewModel,
                userSettings = userSettings,
                strings = strings,
                notifier = notifier
            )
        }
    }
}

//--------------------------------------
// Bottom Navigation Bar
//--------------------------------------
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isDark: Boolean,
    strings: AppStrings
) {
    val items = listOf(
        Triple(Destination.Dashboard, Icons.Rounded.Dashboard, strings.dashboard),
        Triple(Destination.Activity, Icons.Rounded.FitnessCenter, strings.activity),
        Triple(Destination.Nutrition, Icons.Rounded.Fastfood, strings.nutrition),
        Triple(Destination.Progress, Icons.Rounded.Assessment, strings.progress),
        Triple(Destination.Settings, Icons.Rounded.Settings, strings.settings)
    )

    NavigationBar(
        containerColor = if (isDark) Black else White
    ) {
        items.forEach { (dest, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == dest.route,
                onClick = { onNavigate(dest.route) },
                icon = { Icon(icon, contentDescription = label, tint = Lime) },
                label = { Text(label, fontSize = 11.sp, color = Lime) }
            )
        }
    }
}

//--------------------------------------
// Navigation Host
//--------------------------------------
@Composable
fun AppNavHost(
    navController: NavHostController,
    dataViewModel: SharedDataViewModel,
    settingsViewModel: SettingsViewModel,
    userSettings: UserSettings,
    strings: AppStrings,
    notifier: FitTrackNotifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Login.route
    ) {

        //---------------------------
        // Login
        //---------------------------
        composable(Destination.Login.route) {
            LoginScreen(
                strings = strings,
                isDarkMode = userSettings.isDarkMode,
                onLoginSuccess = {
                    notifier.post(FitTrackNotifier.Event.LoggedIn(userSettings.displayName))
                    navController.navigate(Destination.Dashboard.route) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Destination.Register.route)
                }
            )
        }

        //---------------------------
        // Register
        //---------------------------
        composable(Destination.Register.route) {
            RegisterScreen(
                strings = strings,
                isDarkMode = userSettings.isDarkMode,
                onRegisterSuccess = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(Destination.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        //---------------------------
        // Dashboard
        //---------------------------
        composable(Destination.Dashboard.route) {
            DashboardScreen(
                strings = strings,
                isDarkMode = userSettings.isDarkMode,
                viewModel = dataViewModel,
                onNavigate = { /* bottom nav handles */ },
                onSync = dataViewModel::syncNow
            )
        }

        //---------------------------
        // Activity
        //---------------------------
        composable(Destination.Activity.route) {
            ActivityScreen(strings, userSettings.isDarkMode, dataViewModel)
        }

        //---------------------------
        // Nutrition
        //---------------------------
        composable(Destination.Nutrition.route) {
            NutritionScreen(strings, userSettings.isDarkMode, dataViewModel)
        }

        //---------------------------
        // Progress
        //---------------------------
        composable(Destination.Progress.route) {
            ProgressScreen(strings, userSettings.isDarkMode, dataViewModel) {
                navController.navigate(Destination.Achievements.route)
            }
        }

        //---------------------------
        // Achievements
        //---------------------------
        composable(Destination.Achievements.route) {
            AchievementsScreen(strings, userSettings.isDarkMode, dataViewModel)
        }

        //---------------------------
        // Settings
        //---------------------------
        composable(Destination.Settings.route) {
            SettingsScreen(
                strings = strings,
                isDarkMode = userSettings.isDarkMode,
                settingsViewModel = settingsViewModel
            ) {
                notifier.post(FitTrackNotifier.Event.SignedOut(userSettings.displayName))
                navController.navigate(Destination.Login.route) {
                    popUpTo(Destination.Settings.route) { inclusive = true }
                }
            }
        }
    }
}
