package com.example.android_app.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class loginViewModel : ViewModel() {

    // Get Firebase Auth Instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, loginError = null) } // Clear error on edit
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, loginError = null) } // Clear error on edit
    }

    fun onLoginClick() {
        val currentEmail = _uiState.value.email.trim()
        val currentPassword = _uiState.value.password

        // Basic Validation
        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(loginError = "Email and Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            // 1. Set loading state
            _uiState.update { it.copy(isLoading = true, loginError = null) }

            // 2. Firebase Authentication Call
            auth.signInWithEmailAndPassword(currentEmail, currentPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login Success
                        _uiState.update {
                            it.copy(isLoading = false, isLoginSuccess = true)
                        }
                    } else {
                        // Login Failed - Handle specific Firebase exceptions for better UX
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "No account found with this email."
                            is FirebaseAuthInvalidCredentialsException -> "Incorrect password or malformed email."
                            else -> task.exception?.message ?: "Authentication failed."
                        }

                        _uiState.update {
                            it.copy(isLoading = false, loginError = errorMessage)
                        }
                    }
                }
        }
    }
}