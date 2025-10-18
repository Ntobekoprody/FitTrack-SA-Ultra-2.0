package com.fittracksa.app.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.composed
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.data.local.AchievementEntity
import com.fittracksa.app.ui.AppStrings
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
fun ProgressScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel,
    onShowAchievements: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    var weekly by remember { mutableStateOf(true) }

    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    val stats = remember(activities, weekly) { buildStats(activities, weekly) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(strings.progress, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        }

        item {
            SegmentedToggle(weekly = weekly, onToggle = { weekly = it }, isDarkMode = isDarkMode)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Black else White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = if (weekly) "This Week" else "This Month", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    Text(text = "Your fitness journey", color = textColor.copy(alpha = 0.7f))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        StatColumn(label = "Total Workouts", value = stats.totalWorkouts.toString(), isDarkMode = isDarkMode)
                        StatColumn(label = "Active Minutes", value = stats.totalMinutes.toString(), isDarkMode = isDarkMode)
                        StatColumn(label = "Calories Burned", value = stats.caloriesBurned.toString(), isDarkMode = isDarkMode)
                    }
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
                    Text(text = "Activity Trend", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    ActivityTrendChart(activities = activities, isDarkMode = isDarkMode)
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
                    Text(text = "Achievements", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    if (achievements.isEmpty()) {
                        Text(text = "Keep moving to unlock your next milestone.", color = textColor.copy(alpha = 0.7f))
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            achievements.take(3).forEach { badge ->
                                AchievementBadge(badge, isDarkMode)
                            }
                        }
                    }
                    FitPrimaryButton(label = "View Details", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Rounded.Star) {
                        onShowAchievements()
                    }
                }
            }
        }

        item {
            FitSecondaryButton(label = "Sync Progress", leadingIcon = Icons.Rounded.Refresh) {
                viewModel.syncNow()
            }
        }
    }
}

@Composable
private fun SegmentedToggle(weekly: Boolean, onToggle: (Boolean) -> Unit, isDarkMode: Boolean) {
    val background = if (isDarkMode) Black else White
    val activeColor = Lime
    val inactiveColor = if (isDarkMode) Lime.copy(alpha = 0.4f) else Black.copy(alpha = 0.4f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(30.dp))
            .background(background)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ToggleChip(label = "Weekly", selected = weekly, activeColor = activeColor, inactiveColor = inactiveColor) { onToggle(true) }
        ToggleChip(label = "Monthly", selected = !weekly, activeColor = activeColor, inactiveColor = inactiveColor) { onToggle(false) }
    }
}

@Composable
private fun RowScope.ToggleChip(
    label: String,
    selected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp))
            .background(if (selected) activeColor else Color.Transparent)
            .padding(vertical = 12.dp)
            .clickableWithoutRipple(onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = if (selected) Black else inactiveColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActivityTrendChart(activities: List<ActivityEntity>, isDarkMode: Boolean) {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val weekDays = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val totals = weekDays.map { day ->
        activities.filter { it.timestamp.atZone(zone).toLocalDate() == day }.sumOf { it.durationMinutes }
    }
    val max = totals.maxOrNull()?.coerceAtLeast(30) ?: 30

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        totals.forEachIndexed { index, minutes ->
            val ratio = minutes.toFloat() / max.toFloat()
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val barColor = if (minutes > 0) Lime else if (isDarkMode) Lime.copy(alpha = 0.3f) else Black.copy(alpha = 0.1f)
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height((120 * ratio).dp.coerceAtLeast(6.dp))
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .background(barColor)
                )
                Text(text = weekDays[index].dayOfWeek.name.first().toString(), color = if (isDarkMode) Lime else Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String, isDarkMode: Boolean) {
    val textColor = if (isDarkMode) Lime else Black
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, color = Lime, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
private fun AchievementBadge(achievement: AchievementEntity, isDarkMode: Boolean) {
    val borderColor = if (isDarkMode) Lime else Black.copy(alpha = 0.2f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(borderColor)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Star, contentDescription = null, tint = Black)
        }
        Text(achievement.badgeName, color = if (isDarkMode) Lime else Black, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

private fun buildStats(activities: List<ActivityEntity>, weekly: Boolean): ProgressStats {
    val zone = ZoneId.systemDefault()
    val now = Instant.now()
    val cutoff = if (weekly) now.minus(7, ChronoUnit.DAYS) else now.minus(30, ChronoUnit.DAYS)
    val recent = activities.filter { it.timestamp.isAfter(cutoff) }
    val totalMinutes = recent.sumOf { it.durationMinutes }
    val totalWorkouts = recent.size
    val calories = totalMinutes * 5
    return ProgressStats(totalWorkouts, totalMinutes, calories)
}

private data class ProgressStats(val totalWorkouts: Int, val totalMinutes: Int, val caloriesBurned: Int)

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(indication = null, interactionSource = interaction, onClick = onClick)
}
