package com.fittracksa.app.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fittracksa.app.ui.AppStrings
import com.fittracksa.app.ui.auth.AuthViewModel
import com.fittracksa.app.ui.screens.common.FitButton
import com.fittracksa.app.ui.theme.Black
import com.fittracksa.app.ui.theme.White
import com.fittracksa.app.ui.theme.Lime

@Composable
fun RegisterScreen(
    strings: AppStrings,
    isDarkMode: Boolean,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val surface = if (isDarkMode) Black else White
    val titleColor = if (isDarkMode) Lime else Black
    val context = LocalContext.current

    val viewModel: AuthViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = strings.register,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = state.displayName,
            onValueChange = viewModel::updateDisplayName,
            label = { Text(strings.name) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(strings.email) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(strings.password) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        FitButton(label = strings.register) {
            viewModel.register {
                Toast.makeText(context, strings.registerSuccess, Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
            }
        }

        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = Lime)
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(text = it, color = Lime)
        }

        Spacer(Modifier.height(16.dp))

        FitButton(label = strings.backToLogin) {
            onBackToLogin()
        }
    }
}
