package com.fittracksa.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.foundation.shape.RoundedCornerShape
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

        // --------------------------------------------------------------
        // Header Title
        // --------------------------------------------------------------
        item {
            Text(
                text = strings.dashboard,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // --------------------------------------------------------------
        // Statistics Row
        // --------------------------------------------------------------
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DashboardStatCard(
                    title = strings.dayStreak,
                    value = streak.toString(),
                    icon = Icons.Rounded.Timer,
                    isDark = isDarkMode
                )
                DashboardStatCard(
                    title = strings.minActive,
                    value = activeMinutes.toString(),
                    icon = Icons.Rounded.FitnessCenter,
                    isDark = isDarkMode
                )
                DashboardStatCard(
                    title = strings.calories,
                    value = formatCalories(dailyCalories),
                    icon = Icons.Rounded.LocalFireDepartment,
                    isDark = isDarkMode
                )
            }
        }

        // --------------------------------------------------------------
        // Quick Actions
        // --------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = background),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = strings.quickActions,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    FitPrimaryButton(
                        label = strings.logActivity,
                        leadingIcon = Icons.Rounded.FitnessCenter
                    ) { onNavigate(Destination.Activity) }

                    FitPrimaryButton(
                        label = strings.logMeal,
                        leadingIcon = Icons.Rounded.Fastfood
                    ) { onNavigate(Destination.Nutrition) }
                }
            }
        }

        // --------------------------------------------------------------
        // Recent Achievements
        // --------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = background),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = strings.recentAchievements,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    if (achievements.isEmpty()) {
                        Text(
                            text = strings.noAchievementsYet,
                            color = textColor
                        )
                    } else {
                        achievements.take(3).forEach {
                            AchievementRow(it, isDarkMode)
                        }
                    }

                    FitSecondaryButton(
                        label = strings.viewStreaks,
                        leadingIcon = Icons.Rounded.Star
                    ) { onNavigate(Destination.Achievements) }
                }
            }
        }

        // --------------------------------------------------------------
        // Sync Now Button
        // --------------------------------------------------------------
        item {
            FitSecondaryButton(
                label = strings.refresh,
                leadingIcon = Icons.Rounded.Refresh
            ) { onSync() }
        }
    }
}

/* ============================================================================
   Reusable Components
   ============================================================================ */

@Composable
private fun RowScope.DashboardStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    isDark: Boolean
) {
    val cardColor = if (isDark) Black else White
    val textColor = if (isDark) Lime else Black

    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = Lime)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Lime)
            Text(title, color = textColor, fontSize = 14.sp)
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: AchievementEntity,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Lime else Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Black else Lime.copy(alpha = 0.18f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Lime,
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(Modifier.weight(1f)) {
            Text(achievement.badgeName, fontWeight = FontWeight.SemiBold, color = textColor)
            Text(
                achievement.description,
                color = textColor.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}

/* ============================================================================
   Helper Logic
   ============================================================================ */

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

private fun formatCalories(value: Int): String {
    return if (value >= 1000) String.format("%.1fk", value / 1000f)
    else value.toString()
}
