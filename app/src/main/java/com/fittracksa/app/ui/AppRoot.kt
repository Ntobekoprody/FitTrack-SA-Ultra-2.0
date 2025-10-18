package com.fittracksa.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fittracksa.app.SettingsViewModel
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.preferences.UserSettings
import com.fittracksa.app.ui.screens.activity.ActivityScreen
import com.fittracksa.app.ui.screens.achievements.AchievementsScreen
import com.fittracksa.app.ui.screens.dashboard.DashboardScreen
import com.fittracksa.app.ui.screens.login.LoginScreen
import com.fittracksa.app.ui.screens.nutrition.NutritionScreen
import com.fittracksa.app.ui.screens.progress.ProgressScreen
import com.fittracksa.app.ui.screens.settings.SettingsScreen
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Dashboard : Destination("dashboard")
    data object Activity : Destination("activity")
    data object Nutrition : Destination("nutrition")
    data object Progress : Destination("progress")
    data object Settings : Destination("settings")
    data object Achievements : Destination("achievements")
}

data class BottomItem(
    val destination: Destination,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun AppRoot(
    dataViewModel: SharedDataViewModel,
    settingsViewModel: SettingsViewModel,
    userSettings: UserSettings
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val strings = stringsFor(userSettings.language)

    LaunchedEffect(dataViewModel) {
        dataViewModel.events.collect { event ->
            when (event) {
                is SharedDataViewModel.UiEvent.Success ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is SharedDataViewModel.UiEvent.Error ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val bottomItems = listOf(
        BottomItem(Destination.Dashboard, Icons.Rounded.Dashboard, strings.dashboard),
        BottomItem(Destination.Activity, Icons.Rounded.FitnessCenter, strings.activity),
        BottomItem(Destination.Nutrition, Icons.Rounded.Fastfood, strings.nutrition),
        BottomItem(Destination.Progress, Icons.Rounded.Assessment, strings.progress),
        BottomItem(Destination.Settings, Icons.Rounded.Settings, strings.settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Destination.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Black, contentColor = Lime) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.destination.route,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label, tint = Lime) },
                            label = {},
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = Lime,
                                selectedTextColor = Lime,
                                unselectedIconColor = Lime.copy(alpha = 0.7f),
                                indicatorColor = Black
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (userSettings.isDarkMode) Black else White)
        ) {
            AppNavHost(
                navController = navController,
                padding = padding,
                dataViewModel = dataViewModel,
                settingsViewModel = settingsViewModel,
                userSettings = userSettings,
                strings = strings
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    padding: PaddingValues,
    dataViewModel: SharedDataViewModel,
    settingsViewModel: SettingsViewModel,
    userSettings: UserSettings,
    strings: AppStrings
) {
    val isDark = userSettings.isDarkMode

    NavHost(
        navController = navController,
        startDestination = Destination.Login.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(Destination.Login.route) {
            LoginScreen(
                strings = strings,
                isDarkMode = isDark,
                onLoginSuccess = { navController.navigate(Destination.Dashboard.route) {
                    popUpTo(Destination.Login.route) { inclusive = true }
                } }
            )
        }
        composable(Destination.Dashboard.route) {
            DashboardScreen(strings = strings, isDarkMode = isDark, onNavigate = { dest ->
                when (dest) {
                    Destination.Activity -> navController.navigate(Destination.Activity.route)
                    Destination.Nutrition -> navController.navigate(Destination.Nutrition.route)
                    Destination.Progress -> navController.navigate(Destination.Progress.route)
                    Destination.Achievements -> navController.navigate(Destination.Achievements.route)
                    Destination.Settings -> navController.navigate(Destination.Settings.route)
                    else -> Unit
                }
            }, onSync = dataViewModel::syncNow)
        }
        composable(Destination.Activity.route) {
            ActivityScreen(strings = strings, isDarkMode = isDark, viewModel = dataViewModel)
        }
        composable(Destination.Nutrition.route) {
            NutritionScreen(strings = strings, isDarkMode = isDark, viewModel = dataViewModel)
        }
        composable(Destination.Progress.route) {
            ProgressScreen(strings = strings, isDarkMode = isDark, viewModel = dataViewModel, onShowAchievements = {
                navController.navigate(Destination.Achievements.route)
            })
        }
        composable(Destination.Achievements.route) {
            AchievementsScreen(strings = strings, isDarkMode = isDark, viewModel = dataViewModel)
        }
        composable(Destination.Settings.route) {
            SettingsScreen(
                strings = strings,
                isDarkMode = isDark,
                settingsViewModel = settingsViewModel,
                onSignOut = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }
    }
}
