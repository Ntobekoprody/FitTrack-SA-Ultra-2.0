package com.fittracksa.app.ui.screens.login

import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fittracksa.app.biometrics.BiometricAuthenticator
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
    val context = LocalContext.current
    val biometricAuthenticator = remember(context) {
        (context as? ComponentActivity)?.let { BiometricAuthenticator(it) }
    }

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
        FitButton(label = "${strings.biometric} → Dashboard") {
            val helper = biometricAuthenticator
            if (helper == null || !helper.canAuthenticate()) {
                Toast.makeText(context, strings.biometricUnavailable, Toast.LENGTH_SHORT).show()
                return@FitButton
            }
            helper.authenticate(
                title = strings.biometric,
                subtitle = strings.biometricSubtitle,
                negativeButtonText = strings.cancel,
                onSuccess = {
                    Toast.makeText(context, strings.biometric, Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                },
                onError = { message ->
                    val text = if (message == "BIOMETRIC_FAILED") {
                        strings.biometricFailed
                    } else {
                        message
                    }
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.register} → Registration", onClick = onLoginSuccess)
        Spacer(modifier = Modifier.height(16.dp))
        FitButton(label = "${strings.forgotPassword} → Reset Flow", onClick = onLoginSuccess)
    }
}
