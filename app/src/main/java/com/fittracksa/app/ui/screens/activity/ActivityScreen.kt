package com.fittracksa.app.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.local.ActivityEntity
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitPrimaryButton
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel
) {
    val activities by viewModel.activities.collectAsState()

    var activityType by remember { mutableStateOf("Running") }
    var duration by remember { mutableStateOf("30") }
    var expanded by remember { mutableStateOf(false) }

    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black
    val cardColor = if (isDarkMode) Black else White

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = cardColor,
        unfocusedContainerColor = cardColor,
        focusedIndicatorColor = Lime,
        unfocusedIndicatorColor = textColor.copy(alpha = 0.4f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = Lime
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text(
                text = strings.activity,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.FitnessCenter, null, tint = Lime)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = strings.logActivity,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    }

                    // Activity type picker
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = activityType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(strings.selectType) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        val options = listOf(
                            "Running","Walking","Cycling",
                            "Swimming","Gym Workout","Yoga","Sports"
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        activityType = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it.filter(Char::isDigit) },
                        label = { Text(strings.setDuration) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Schedule, null, tint = Lime)
                        },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        FitPrimaryButton(
                            label = strings.saveActivity,
                            leadingIcon = Icons.Rounded.Check,
                            modifier = Modifier.weight(1f)
                        ) {
                            val minutes = duration.toIntOrNull() ?: 0
                            if (minutes > 0) {
                                viewModel.logActivity(activityType, minutes)
                                activityType = "Running"
                                duration = "30"
                            }
                        }

                        FitSecondaryButton(
                            label = strings.cancel,
                            leadingIcon = Icons.Rounded.Close,
                            modifier = Modifier.weight(1f)
                        ) {
                            activityType = "Running"
                            duration = "30"
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Recent Activities",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    if (activities.isEmpty()) {
                        Text(
                            text = "No activities yet.",
                            color = textColor.copy(alpha = 0.7f)
                        )
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("h:mm a")
                        activities.take(5).forEach { activity ->
                            ActivityListRow(activity, formatter, isDarkMode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityListRow(
    activity: ActivityEntity,
    formatter: DateTimeFormatter,
    isDarkMode: Boolean
) {
    val textColor = if (isDarkMode) Lime else Black
    val zone = ZoneId.systemDefault()
    val timestamp = activity.timestamp.atZone(zone)
    val today = LocalDate.now(zone)
    val daysBetween = ChronoUnit.DAYS.between(timestamp.toLocalDate(), today)

    val relativeDay = when (daysBetween) {
        0L -> "Today"
        1L -> "Yesterday"
        else -> timestamp.dayOfWeek.name.lowercase().replaceFirstChar(Char::titlecase)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(activity.type, fontWeight = FontWeight.SemiBold, color = textColor)
            Text("${activity.durationMinutes} min", color = textColor.copy(alpha = 0.7f))
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(relativeDay, color = textColor, fontSize = 12.sp)
            Text(formatter.format(timestamp), color = textColor.copy(alpha = 0.7f))
        }
    }
}
