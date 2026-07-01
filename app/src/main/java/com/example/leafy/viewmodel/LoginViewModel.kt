package com.example.leafy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafy.firebase.AuthRepository
import com.example.leafy.model.User
import kotlinx.coroutines.launch

data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,   // true = login, false = registro
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun toggleMode() {
        uiState = uiState.copy(
            isLoginMode = !uiState.isLoginMode,
            error = null
        )
    }

    fun submit(onSuccess: (User) -> Unit) {
        val email = uiState.email.trim()
        val password = uiState.password.trim()
        val name = uiState.name.trim()

        if (email.isEmpty() || password.isEmpty() ||
            (!uiState.isLoginMode && name.isEmpty())
        ) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val user = if (uiState.isLoginMode) {
                repo.login(email, password)
            } else {
                repo.register(name, email, password)
            }

            if (user != null) {
                uiState = uiState.copy(isLoading = false)
                onSuccess(user)
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error al ${if (uiState.isLoginMode) "iniciar sesión" else "registrarse"}"
                )
            }
        }
    }
}