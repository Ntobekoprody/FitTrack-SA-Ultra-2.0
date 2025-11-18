package com.fittracksa.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittracksa.app.domain.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authManager: AuthManager = AuthManager()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun updateEmail(value: String) { _state.value = _state.value.copy(email = value) }
    fun updatePassword(value: String) { _state.value = _state.value.copy(password = value) }
    fun updateDisplayName(value: String) { _state.value = _state.value.copy(displayName = value) }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val result = authManager.login(_state.value.email, _state.value.password)
            _state.value = _state.value.copy(loading = false)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val result = authManager.register(_state.value.email, _state.value.password, _state.value.displayName)
            _state.value = _state.value.copy(loading = false)
            if (result.isSuccess) onSuccess()
            else _state.value = _state.value.copy(error = result.exceptionOrNull()?.message ?: "Registration failed")
        }
    }

    fun updateUsername(newName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val result = authManager.updateDisplayName(newName)
            _state.value = _state.value.copy(loading = false)
            if (result.isSuccess) onSuccess()
            else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to update name"
                _state.value = _state.value.copy(error = msg)
                onFailure(msg)
            }
        }
    }

    fun logout() {
        authManager.logout()
    }

    fun currentUserName(): String? = authManager.currentUserName()
}
