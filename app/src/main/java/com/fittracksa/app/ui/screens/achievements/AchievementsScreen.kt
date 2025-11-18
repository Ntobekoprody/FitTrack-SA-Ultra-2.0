package com.fittracksa.app.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.SharedDataViewModel
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun AchievementsScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    viewModel: SharedDataViewModel
) {
    val achievements by viewModel.achievements.collectAsState()
    val surface = if (isDarkMode) Black else White
    val cardColor = if (isDarkMode) Black else Lime
    val textColor = if (isDarkMode) Lime else Black
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp)
    ) {
        Text(strings.achievements, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            items(achievements, key = { it.id }) { achievement ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardColor)
                        .padding(16.dp)
                ) {
                    Text(text = achievement.badgeName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = achievement.description, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { viewModel.joinChallenge(achievement.id) },
                            colors = ButtonDefaults.textButtonColors(contentColor = textColor)
                        ) {
                            Text(strings.joinChallenge, color = textColor)
                        }
                        Text(text = "${strings.seeStreakDetails}: ${achievement.streakDays} days", color = textColor)
                    }
                }
            }
        }
    }
}
