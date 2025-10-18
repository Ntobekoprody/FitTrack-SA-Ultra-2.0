package com.fittracksa.app.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitPrimaryButton
import com.fittracksa.app.ui.screens.common.FitSecondaryButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel
) {
    val meals by viewModel.meals.collectAsState()
    var description by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    val background = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black
    val cardColor = if (isDarkMode) Black else White

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = cardColor,
        unfocusedContainerColor = cardColor,
        focusedIndicatorColor = Lime,
        unfocusedIndicatorColor = if (isDarkMode) Lime.copy(alpha = 0.4f) else Black.copy(alpha = 0.1f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = textColor,
        unfocusedLabelColor = textColor.copy(alpha = 0.7f),
        cursorColor = Lime
    )

    val todayIntake = meals.filterTodayCalories()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(strings.nutrition, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Today's Intake", fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.7f))
                    Text(text = todayIntake.formatCalories(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Lime)
                    Text(text = "Goal 2,000 cal", color = textColor.copy(alpha = 0.7f))
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(imageVector = Icons.Rounded.Fastfood, contentDescription = null, tint = Lime)
                        Column {
                            Text(text = strings.logMeal, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                            Text(text = "Track your nutrition", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Food name") },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = calories,
                        onValueChange = { value -> calories = value.filter { it.isDigit() } },
                        label = { Text("Calories") },
                        leadingIcon = { Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Lime) },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        FitPrimaryButton(
                            label = strings.saveMeal,
                            leadingIcon = Icons.Rounded.Check,
                            modifier = Modifier.weight(1f)
                        ) {
                            val kcal = calories.toIntOrNull() ?: 0
                            if (description.isNotBlank() && kcal > 0) {
                                viewModel.logMeal(description, kcal)
                                description = ""
                                calories = ""
                            }
                        }
                        FitSecondaryButton(
                            label = strings.cancel,
                            leadingIcon = Icons.Rounded.Close,
                            modifier = Modifier.weight(1f)
                        ) {
                            description = ""
                            calories = ""
                        }
                    }

                    Text(text = "Saves to local device · Auto-syncs when online", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ShortcutCard(title = "Breakfast", icon = Icons.Rounded.Restaurant, isDarkMode = isDarkMode) {
                    description = "Breakfast"
                    calories = "450"
                }
                ShortcutCard(title = "Lunch", icon = Icons.Rounded.LocalDining, isDarkMode = isDarkMode) {
                    description = "Lunch"
                    calories = "600"
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Recent Meals", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    if (meals.isEmpty()) {
                        Text(text = "Capture your meals to monitor calories.", color = textColor.copy(alpha = 0.7f))
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("h:mm a")
                        meals.take(6).forEach { meal ->
                            MealRow(meal = meal, formatter = formatter, isDarkMode = isDarkMode, onDelete = {
                                viewModel.deleteMeal(meal)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val container = if (isDarkMode) Black else White
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Lime)
            Text(title, color = if (isDarkMode) Lime else Black, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun MealRow(meal: MealEntity, formatter: DateTimeFormatter, isDarkMode: Boolean, onDelete: () -> Unit) {
    val textColor = if (isDarkMode) Lime else Black
    val zone = ZoneId.systemDefault()
    val timestamp = meal.timestamp.atZone(zone)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(meal.description, fontWeight = FontWeight.SemiBold, color = textColor)
            Text("${meal.calories} cal", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(formatter.format(timestamp), color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "Delete", tint = textColor)
            }
        }
    }
}

private fun List<MealEntity> filterTodayCalories(): Int {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    return filter { it.timestamp.atZone(zone).toLocalDate() == today }.sumOf { it.calories }
}

private fun Int.formatCalories(): String = String.format("%,d", this)
