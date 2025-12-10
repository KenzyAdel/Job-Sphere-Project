package com.example.android_app.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    // Auth Flow
    @Serializable data object Login : Screen
    @Serializable data object RoleSelection : Screen
    @Serializable data object ApplicantSignUp : Screen
    @Serializable data object CompanySignUp : Screen

    // Main Flows
    @Serializable data object CompanyHome : Screen
    @Serializable data object ApplicantHome : Screen
}