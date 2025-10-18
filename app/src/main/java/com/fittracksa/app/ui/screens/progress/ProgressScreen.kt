package com.fittracksa.app.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White
import java.time.format.DateTimeFormatter

@Composable
fun ProgressScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel,
    onShowAchievements: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("MMM dd")
    val weeklyMode = remember { mutableStateOf(true) }
    val filterExpanded = remember { mutableStateOf(false) }
    val showAll = remember { mutableStateOf(false) }
    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(strings.progress, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        FitButton(label = "${strings.weeklyMonthly} → Toggle chart span") {
            weeklyMode.value = !weeklyMode.value
        }
        Spacer(modifier = Modifier.height(12.dp))
        FitButton(label = "${strings.filterRange} → Offline filter | RoomDB read") {
            filterExpanded.value = !filterExpanded.value
        }
        Spacer(modifier = Modifier.height(12.dp))
        FitButton(label = "${strings.viewDetails} → Activity history read") {
            showAll.value = !showAll.value
        }
        Spacer(modifier = Modifier.height(12.dp))
        FitButton(label = "${strings.viewStreaks} → Achievements Screen | RoomDB read") {
            onShowAchievements()
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "${if (weeklyMode.value) "Week" else "Month"} summary: ${activities.take(5).sumOf { it.durationMinutes }} mins",
            color = textColor,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val items = if (showAll.value) activities else activities.take(5)
        items.forEach { activity ->
            Text(
                text = "${activity.type} · ${activity.durationMinutes} mins · ${activity.timestamp.atZone(java.time.ZoneId.systemDefault()).format(formatter)}",
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (filterExpanded.value) {
            Text(
                text = "Filtered locally; unsynced entries remain available until cloud sync completes.",
                color = textColor,
                fontSize = 12.sp
            )
        }
    }
}
