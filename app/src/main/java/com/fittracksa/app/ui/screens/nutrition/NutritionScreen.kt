package com.fittracksa.app.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.data.local.MealEntity
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun NutritionScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel
) {
    val meals by viewModel.meals.collectAsState()
    var description by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(
            text = strings.nutrition,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(strings.addFood, color = textColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = textColor,
                unfocusedBorderColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                focusedContainerColor = surface,
                unfocusedContainerColor = surface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it.filter { char -> char.isDigit() } },
            label = { Text("Calories", color = textColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = textColor,
                unfocusedBorderColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor,
                focusedContainerColor = surface,
                unfocusedContainerColor = surface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FitButton(
                label = "${strings.useShortcut} → Pre-fill snack",
                modifier = Modifier.weight(1f)
            ) {
                description = "Hydration + Fruit"
                calories = "180"
            }
            FitButton(
                label = "${strings.saveMeal} → RoomDB; sync later",
                modifier = Modifier.weight(1f)
            ) {
                val kcal = calories.toIntOrNull() ?: 0
                if (description.isNotBlank() && kcal > 0) {
                    viewModel.logMeal(description, kcal)
                    description = ""
                    calories = ""
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(meals, key = { it.id }) { meal ->
                MealRow(strings, isDarkMode, meal, onEdit = { updated ->
                    viewModel.updateMeal(updated)
                }, onDelete = {
                    viewModel.deleteMeal(meal)
                })
            }
        }
    }
}

@Composable
private fun MealRow(
    strings: AppStrings,
    isDarkMode: Boolean,
    meal: MealEntity,
    onEdit: (MealEntity) -> Unit,
    onDelete: () -> Unit
) {
    val cardColor = if (isDarkMode) Black else Lime
    val textColor = if (isDarkMode) Lime else Black
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor)
            .padding(16.dp)
    ) {
        Text(text = "${meal.description} · ${meal.calories} kcal", color = textColor, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = { onEdit(meal.copy(description = meal.description + " ✎", isSynced = false)) },
                colors = ButtonDefaults.textButtonColors(contentColor = textColor)
            ) {
                Text(text = strings.editDelete, color = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = textColor)
            ) {
                Text(text = "Delete", color = textColor)
            }
        }
    }
}
