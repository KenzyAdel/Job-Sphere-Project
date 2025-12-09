package com.example.android_app.presentation.applicant.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppSignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AppSignUpUiState())
    val uiState: StateFlow<AppSignUpUiState> = _uiState.asStateFlow()

    // --- State Update Functions ---
    fun onFullNameChange(newValue: String) {
        _uiState.update { it.copy(fullName = newValue, signUpError = null) }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue, signUpError = null) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue, signUpError = null) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.update { it.copy(confirmPassword = newValue, signUpError = null) }
    }

    fun onPhoneChange(newValue: String) {
        _uiState.update { it.copy(phone = newValue) }
    }

    fun onLinkedInChange(newValue: String) {
        _uiState.update { it.copy(linkedIn = newValue) }
    }

    fun onPortfolioChange(newValue: String) {
        _uiState.update { it.copy(portfolio = newValue) }
    }

    // --- Sign Up Logic ---
    fun onSignUpClick() {
        val state = _uiState.value

        // 1. Basic Validation
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(signUpError = "Please fill in all required fields.") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(signUpError = "Passwords do not match.") }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(signUpError = "Password must be at least 6 characters.") }
            return
        }

        // 2. Perform Firebase Registration
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signUpError = null) }

            auth.createUserWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        // Note: You might want to save the additional fields (Phone, LinkedIn, etc.)
                        // to Firestore or Realtime Database here, as Auth only stores basic info.
                        _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                    } else {
                        // Registration failed
                        val errorMsg = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "An account with this email already exists."
                            else -> task.exception?.message ?: "Sign up failed. Please try again."
                        }
                        _uiState.update { it.copy(isLoading = false, signUpError = errorMsg) }
                    }
                }
        }
    }
}