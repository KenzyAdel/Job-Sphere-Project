package com.example.android_app.presentation.company.signup

data class companySignUpUiState(
    val companyName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Optional fields
    val website: String = "",
    val phone: String = "",
    val industry: String = "",
    val linkedIn: String = "",

    val isLoading: Boolean = false,
    val signUpError: String? = null,
    val isSignUpSuccess: Boolean = false
)