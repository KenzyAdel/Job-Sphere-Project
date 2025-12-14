package com.example.android_app.presentation.applicant.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.services.CompanyService
import com.example.android_app.data.services.JobService
import com.example.android_app.data.repositories.SavedJobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.example.android_app.data.services.ApplicationService
import com.example.android_app.data.services.ApplicantService
import com.example.android_app.data.models.Application

class ApplicantHomeViewModel(
    private val jobService: JobService = JobService(),
    private val companyService: CompanyService = CompanyService(),
    private val applicationService: ApplicationService = ApplicationService(),
    private val applicantService: ApplicantService = ApplicantService(),
    private val savedJobRepository: SavedJobRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : ViewModel() {

    private val applicantId: String
        get() = auth.currentUser?.uid ?: "test_applicant_001"

    private val _state = MutableStateFlow(ApplicantHomeState())
    val state: StateFlow<ApplicantHomeState> = _state.asStateFlow()
    
    private var cachedJobs: List<com.example.android_app.data.models.Job> = emptyList()

    init {
        loadJobs()
        loadSavedJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val companiesResult = companyService.getAllCompanies()
            val jobsResult = jobService.getAllJobs()

            if (jobsResult.isSuccess && companiesResult.isSuccess) {
                val companies = companiesResult.getOrNull() ?: emptyList()
                val jobs = jobsResult.getOrNull() ?: emptyList()

                cachedJobs = jobs

                val companyNameMap = companies.associate { it.id to it.name }

                val uiJobs = jobs.mapNotNull { job ->
                    if (job.title.isBlank()) return@mapNotNull null

                    JobItem(
                        id = job.id,
                        companyId = job.companyId, // Include companyId for navigation
                        title = job.title,
                        company = companyNameMap[job.companyId] ?: "Unknown Company",
                        location = job.location,
                        salary = job.salary,
                        description = job.description,
                        responsibilities = job.responsibilities,
                        requirements = job.requirements
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        jobs = uiJobs,
                        allJobsCached = uiJobs
                    )
                }
            } else {

                val error = jobsResult.exceptionOrNull()?.message
                    ?: companiesResult.exceptionOrNull()?.message
                    ?: "Unknown error occurred"

                _state.update { it.copy(isLoading = false, errorMessage = error) }
            }
        }
    }


    fun onSaveJob(job: JobItem) {
        println("=== onSaveJob CALLED ===")
        println("Job ID: ${job.id}, Title: ${job.title}")
        
        viewModelScope.launch {
            val jobId = job.id
            val isSaved = _state.value.savedJobIds.contains(jobId)
            
            println("Is job saved? $isSaved")
            println("Current saved IDs: ${_state.value.savedJobIds}")
            
            try {
                if (isSaved) {
                    println("Attempting to UNSAVE job...")
                    val entityToRemove = _state.value.savedJobsList.find { it.id == jobId }
                    
                    if (entityToRemove != null) {
                        savedJobRepository.removeJob(entityToRemove)
                        println("✓ Unsaved job locally: ${job.title}")
                    } else {
                        println("✗ Error: Could not find job entity to remove for ID: $jobId")
                    }
                } else {
                    println("Attempting to SAVE job...")
                    println("Calling savedJobRepository.saveJob($jobId)")
                    savedJobRepository.saveJob(jobId)
                    println("✓ Save operation completed for: ${job.title}")
                }
            } catch (e: Exception) {
                println("✗✗✗ ERROR saving/unsaving job locally: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    private fun loadSavedJobs() {
        viewModelScope.launch {
            try {
                savedJobRepository.getSavedJobs().collect { savedEntities ->
                    val savedIds = savedEntities.map { it.id }.toSet()
                    _state.update { 
                        it.copy(
                            savedJobIds = savedIds,
                            savedJobsList = savedEntities
                        ) 
                    }
                }
            } catch (e: Exception) {
                println("Error loading saved jobs from repository: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}