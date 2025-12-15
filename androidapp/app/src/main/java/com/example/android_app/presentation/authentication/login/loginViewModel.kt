package com.example.android_app.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.presentation.navigation.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class loginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, loginError = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, loginError = null) }
    }

    fun onLoginClick() {
        val currentEmail = _uiState.value.email.trim()
        val currentPassword = _uiState.value.password

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _uiState.update { it.copy(loginError = "Email and Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }

            try {
                val authResult = auth.signInWithEmailAndPassword(currentEmail, currentPassword).await()
                val userId = authResult.user?.uid

                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()

                    // 1. Check Company Collection
                    val companyDoc = db.collection("Company").document(userId).get().await()

                    if (companyDoc.exists()) {
                        // ---> Found Company! Update state with COMPANY type
                        _uiState.update {
                            it.copy(isLoading = false, loginSuccessUserType = UserType.COMPANY)
                        }
                    } else {
                        // 2. Check Applicant Collection
                        val applicantDoc = db.collection("Applicant").document(userId).get().await()

                        if (applicantDoc.exists()) {
                            // ---> Found Applicant! Update state with APPLICANT type
                            _uiState.update {
                                it.copy(isLoading = false, loginSuccessUserType = UserType.APPLICANT)
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, loginError = "User profile not found.")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password."
                    else -> e.message ?: "Authentication failed."
                }
                _uiState.update { it.copy(isLoading = false, loginError = errorMessage) }
            }
        }
    }

    // Helper to reset state after navigation
    fun onLoginSuccessHandled() {
        _uiState.update { it.copy(loginSuccessUserType = null) }
    }
}