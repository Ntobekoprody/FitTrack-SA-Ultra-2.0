package com.fittracksa.app.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel
) {
    var activityType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(
            text = strings.activity,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = activityType,
            onValueChange = { activityType = it },
            label = { Text(strings.selectType, color = textColor) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                focusedContainerColor = surface,
                unfocusedContainerColor = surface,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                cursorColor = textColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            textStyle = TextStyle(color = textColor),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it.filter { char -> char.isDigit() } },
            label = { Text(strings.setDuration, color = textColor) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = textColor,
                unfocusedIndicatorColor = textColor,
                focusedContainerColor = surface,
                unfocusedContainerColor = surface,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.8f),
                cursorColor = textColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            textStyle = TextStyle(color = textColor),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.saveActivity} → RoomDB; sync later") {
            val minutes = duration.toIntOrNull() ?: 0
            if (activityType.isNotBlank() && minutes > 0) {
                viewModel.logActivity(activityType, minutes)
                activityType = ""
                duration = ""
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        FitButton(label = "${strings.cancel} → Clear form") {
            activityType = ""
            duration = ""
        }
    }
}
