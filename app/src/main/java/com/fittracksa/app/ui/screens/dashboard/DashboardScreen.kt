package com.fittracksa.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.Destination
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun DashboardScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    onNavigate: (Destination) -> Unit,
    onSync: () -> Unit
) {
    val surface = if (isDarkMode) Black else White
    val textColor = if (isDarkMode) Lime else Black
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(
            text = strings.dashboard,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        FitButton(label = "${strings.logActivity} → Activity Screen | Save to RoomDB; sync pending") {
            onNavigate(Destination.Activity)
        }
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.logMeal} → Nutrition Screen | Save to RoomDB; sync pending") {
            onNavigate(Destination.Nutrition)
        }
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.viewStreaks} → Achievements Screen | Read from RoomDB") {
            onNavigate(Destination.Achievements)
        }
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.refresh} → Manual sync with cloud") {
            onSync()
        }
    }
}
