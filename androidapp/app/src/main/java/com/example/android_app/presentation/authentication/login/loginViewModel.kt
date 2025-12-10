package com.example.android_app.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class loginViewModel @Inject constructor(): ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, loginError = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, loginError = null) }
    }

    // Call this AFTER navigating to reset the state
    fun onLoginSuccessNavigated() {
        _uiState.update { it.copy(loginSuccessRole = null) }
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

                    // Check Company Collection
                    val companyDoc = db.collection("Company").document(userId).get().await()

                    if (companyDoc.exists()) {
                        // ---> User is a Company
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginSuccessRole = UserRole.COMPANY // Set specific role
                            )
                        }
                    } else {
                        // Check Applicant Collection
                        val applicantDoc = db.collection("Applicant").document(userId).get().await()

                        if (applicantDoc.exists()) {
                            // ---> User is an Applicant
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loginSuccessRole = UserRole.APPLICANT // Set specific role
                                )
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
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password or malformed email."
                    else -> e.message ?: "Authentication failed."
                }

                _uiState.update {
                    it.copy(isLoading = false, loginError = errorMessage)
                }
            }
        }
    }
}