package com.example.android_app.presentation.authentication.login

import com.example.android_app.presentation.navigation.UserType

data class loginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccessUserType: UserType? = null)
