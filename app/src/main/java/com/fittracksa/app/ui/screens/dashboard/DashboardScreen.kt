package com.fittracksa.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.local.AchievementEntity
import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.Destination
import com.fittracksa.app.ui.screens.common.FitPrimaryButton
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun DashboardScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel,
    onNavigate: (Destination) -> Unit,
    onSync: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    val streak = currentStreak(activities)
    val activeMinutes = recentMinutes(activities)
    val dailyCalories = todayCalories(meals)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = strings.dashboard,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                DashboardStatCard(
                    title = "Day Streak",
                    value = streak.toString(),
                    icon = Icons.Rounded.Timer,
                    isDark = isDarkMode
                )
                DashboardStatCard(
                    title = "Min Active",
                    value = activeMinutes.toString(),
                    icon = Icons.Rounded.FitnessCenter,
                    isDark = isDarkMode
                )
                DashboardStatCard(
                    title = "Calories",
                    value = formatCalories(dailyCalories),
                    icon = Icons.Rounded.LocalFireDepartment,
                    isDark = isDarkMode
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Black else White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Quick Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    FitPrimaryButton(
                        label = strings.logActivity,
                        leadingIcon = Icons.Rounded.FitnessCenter
                    ) { onNavigate(Destination.Activity) }
                    FitPrimaryButton(
                        label = strings.logMeal,
                        leadingIcon = Icons.Rounded.LocalFireDepartment
                    ) { onNavigate(Destination.Nutrition) }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Black else White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Recent Achievements", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    if (achievements.isEmpty()) {
                        Text(
                            text = "Keep logging to unlock your next badge.",
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        achievements.take(3).forEach { achievement ->
                            AchievementRow(achievement = achievement, isDarkMode = isDarkMode)
                        }
                    }
                    FitSecondaryButton(
                        label = strings.viewStreaks,
                        leadingIcon = Icons.Rounded.Star
                    ) { onNavigate(Destination.Achievements) }
                }
            }
        }

        item {
            FitSecondaryButton(
                label = strings.refresh,
                leadingIcon = Icons.Rounded.Refresh
            ) { onSync() }
        }
    }
}

@Composable
private fun RowScope.DashboardStatCard(title: String, value: String, icon: ImageVector, isDark: Boolean) {
    val container = if (isDark) Black else White
    val textColor = if (isDark) Lime else Black
    Card(
        modifier = Modifier.weight(1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = Lime)
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Lime)
            Text(text = title, color = textColor, fontSize = 14.sp)
        }
    }
}

@Composable
private fun AchievementRow(achievement: AchievementEntity, isDarkMode: Boolean) {
    val textColor = if (isDarkMode) Lime else Black
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Black else Lime.copy(alpha = 0.2f)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Lime,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(text = achievement.badgeName, fontWeight = FontWeight.SemiBold, color = textColor)
            Text(
                text = achievement.description,
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun currentStreak(activities: List<ActivityEntity>): Int {
    if (activities.isEmpty()) return 0
    val zone = ZoneId.systemDefault()
    val days = activities.map { it.timestamp.atZone(zone).toLocalDate() }.toSet()
    var streak = 0
    var cursor = LocalDate.now(zone)
    while (days.contains(cursor)) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

private fun recentMinutes(activities: List<ActivityEntity>): Int {
    val cutoff = Instant.now().minus(7, ChronoUnit.DAYS)
    return activities.filter { it.timestamp.isAfter(cutoff) }.sumOf { it.durationMinutes }
}

private fun todayCalories(meals: List<MealEntity>): Int {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    return meals.filter { it.timestamp.atZone(zone).toLocalDate() == today }.sumOf { it.calories }
}

private fun formatCalories(calories: Int): String {
    return if (calories >= 1000) String.format("%.1fk", calories / 1000f) else calories.toString()
}
