package com.example.android_app.presentation.applicant.jobdetails

data class JobDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val job: JobPosting? = null,
    val isApplying: Boolean = false,
    val isApplicationSuccess: Boolean = false,
    val applicationError: String? = null,
    val currentCompanyId: String = "",
    val currentJobId: String = "",
    val isSaved: Boolean = false,
    val companyName: String = ""
)

data class JobPosting(
    val title: String = "",
    val location: String = "",
    val salary: String = "",
    val description: String = "",
    val responsibilities: String = "",
    val requirements: String = ""
)