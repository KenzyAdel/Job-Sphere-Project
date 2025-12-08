package com.example.android_app.presentation.authentication.login

data class loginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val isLoginSuccess: Boolean = false
)
