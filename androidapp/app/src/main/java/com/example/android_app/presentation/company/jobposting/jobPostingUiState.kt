package com.example.android_app.presentation.company.jobposting

data class JobPostingUiState(
    val title: String = "",
    val location: String = "",
    val salary: String = "",
    val description: String = "",
    val responsibilities: String = "",
    val requirements: String = "",
    val selectedJobType: String = "Full Time",
    val isLoading: Boolean = false,
    val isPostSuccess: Boolean = false,
    val errorMessage: String? = null
)
