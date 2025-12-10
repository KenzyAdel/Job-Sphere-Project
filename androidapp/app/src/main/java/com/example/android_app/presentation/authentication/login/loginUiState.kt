package com.example.android_app.presentation.authentication.login

// Enum to define the user types
enum class UserRole {
    COMPANY,
    APPLICANT
}

data class loginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccessRole: UserRole? = null
)