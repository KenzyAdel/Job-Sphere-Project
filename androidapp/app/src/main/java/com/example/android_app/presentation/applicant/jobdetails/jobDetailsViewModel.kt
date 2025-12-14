package com.example.android_app.presentation.applicant.jobdetails
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.models.Application
import com.example.android_app.data.services.ApplicationService
import com.example.android_app.data.services.ApplicantService
import com.example.android_app.data.services.CompanyService
import com.example.android_app.data.services.JobService
import com.example.android_app.data.repositories.SavedJobRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JobDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val jobService: JobService = JobService(),
    private val companyService: CompanyService = CompanyService(),
    private val applicationService: ApplicationService = ApplicationService(),
    private val applicantService: ApplicantService = ApplicantService(),
    private val savedJobRepository: SavedJobRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(JobDetailsUiState())
    val state: StateFlow<JobDetailsUiState> = _state.asStateFlow()
    
    private val companyId: String = checkNotNull(savedStateHandle["companyId"])
    private val jobId: String = checkNotNull(savedStateHandle["jobId"])
    
    private val applicantId: String
        get() = auth.currentUser?.uid ?: ""

    init {
        loadJobDetails()
        observeSavedStatus()
    }

    private fun loadJobDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val jobResult = jobService.getJob(companyId, jobId)
            val companyResult = companyService.getCompany(companyId)

            if (jobResult.isSuccess) {
                val domainJob = jobResult.getOrNull()
                val company = companyResult.getOrNull()
                
                if (domainJob != null) {
                    val uiJob = JobPosting(
                        title = domainJob.title,
                        location = domainJob.location,
                        salary = domainJob.salary,
                        description = domainJob.description,
                        responsibilities = domainJob.responsibilities,
                        requirements = domainJob.requirements
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            job = uiJob,
                            currentCompanyId = companyId,
                            currentJobId = jobId,
                            companyName = company?.name ?: "Unknown Company"
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Job not found") }
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = jobResult.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }

    private fun observeSavedStatus() {
        viewModelScope.launch {
            savedJobRepository.getSavedJobs().collect { savedJobs ->
                val isSaved = savedJobs.any { it.id == jobId }
                _state.update { it.copy(isSaved = isSaved) }
            }
        }
    }

    fun applyToJob(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isApplying = true, applicationError = null) }

            if (applicantId.isEmpty()) {
                _state.update { it.copy(isApplying = false) }
                onFailure("User not logged in")
                return@launch
            }

            val applicantResult = applicantService.getApplicant(applicantId)
            val applicant = applicantResult.getOrNull()

            if (applicant != null) {
                val application = Application(
                    id = applicantId,
                    jobId = jobId,
                    companyId = companyId,
                    status = "Pending",
                    name = applicant.name,
                    email = applicant.email,
                    phone = applicant.phone,
                    cvLink = applicant.cvLink,
                    linkedIn = applicant.linkedin
                )

                val result = applicationService.createApplicationWithId(
                    companyId = companyId,
                    jobId = jobId,
                    applicationId = applicantId,
                    application = application
                )

                if (result.isSuccess) {
                    _state.update { it.copy(isApplying = false, isApplicationSuccess = true) }
                    onSuccess()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to apply"
                    _state.update { it.copy(isApplying = false, applicationError = error) }
                    onFailure(error)
                }
            } else {
                val error = "Could not fetch your profile. Please complete your profile first."
                _state.update { it.copy(isApplying = false, applicationError = error) }
                onFailure(error)
            }
        }
    }

    fun toggleSaveJob() {
        viewModelScope.launch {
            try {
                if (_state.value.isSaved) {
                    savedJobRepository.getSavedJobs().collect { savedJobs ->
                        val jobToRemove = savedJobs.find { it.id == jobId }
                        if (jobToRemove != null) {
                            savedJobRepository.removeJob(jobToRemove)
                        }
                        return@collect
                    }
                } else {
                    savedJobRepository.saveJob(jobId)
                }
            } catch (e: Exception) {
                println("Error toggling save status: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}