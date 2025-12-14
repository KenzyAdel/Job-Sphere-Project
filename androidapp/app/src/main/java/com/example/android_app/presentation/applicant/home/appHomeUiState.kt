package com.example.android_app.presentation.applicant.home


data class ApplicantHomeState(
    val isLoading: Boolean = false,
    val jobs: List<JobItem> = emptyList(),
    val allJobsCached: List<JobItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedFilter: String = "All",
    val savedJobIds: Set<String> = emptySet(),
    val savedJobsList: List<com.example.android_app.data.entities.SavedJobsEntity> = emptyList()
)