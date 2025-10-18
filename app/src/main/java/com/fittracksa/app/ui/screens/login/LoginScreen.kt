package com.fittracksa.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.Lime
import com.fittracksa.app.ui.theme.White

@Composable
fun LoginScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    onLoginSuccess: () -> Unit
) {
    val surface = if (isDarkMode) Black else White
    val titleColor = if (isDarkMode) Lime else Black
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = strings.loginTitle,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        FitButton(label = "${strings.emailLogin} → Dashboard", onClick = onLoginSuccess)
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.googleSso} → Dashboard", onClick = onLoginSuccess)
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.biometric} → Dashboard", onClick = onLoginSuccess)
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.register} → Registration", onClick = onLoginSuccess)
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.forgotPassword} → Reset Flow", onClick = onLoginSuccess)
    }
}
