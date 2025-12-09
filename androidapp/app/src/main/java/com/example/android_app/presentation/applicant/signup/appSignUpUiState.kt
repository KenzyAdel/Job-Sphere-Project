package com.example.android_app.presentation.applicant.signup

data class AppSignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Optional fields
    val phone: String = "",
    val linkedIn: String = "",
    val portfolio: String = "",
    val isLoading: Boolean = false,
    val signUpError: String? = null,
    val isSignUpSuccess: Boolean = false
)