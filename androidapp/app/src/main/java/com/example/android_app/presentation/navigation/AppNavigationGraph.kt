package com.example.android_app.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android_app.data.services.JobService
import com.example.android_app.presentation.applicant.home.ApplicantHomeScreen
import com.example.android_app.presentation.applicant.home.JobItem
import com.example.android_app.presentation.applicant.home.ApplicantHomeViewModel
import com.example.android_app.presentation.applicant.jobdetails.JobPosting
import com.example.android_app.presentation.applicant.jobdetails.JobPostingDetailsScreen
import com.example.android_app.presentation.applicant.savedJobs.SavedJobsScreen
import com.example.android_app.presentation.applicant.savedJobs.savedJobsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.android_app.data.repositories.SavedJobRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.example.android_app.data.DAOs.SavedJobDao
import com.example.android_app.data.database.AppDatabase
import com.example.android_app.presentation.applicant.signup.ApplicantSignUpScreen
import com.example.android_app.presentation.authentication.login.LoginScreen
import com.example.android_app.presentation.company.applicationManagement.ApplicantCard
import com.example.android_app.presentation.company.applicationManagement.ApplicationManagementScreen
import com.example.android_app.presentation.company.applicationManagement.TempApplicantUiItem
import com.example.android_app.presentation.company.home.CompanyHomeScreen
import com.example.android_app.presentation.company.home.CompanyHomeRoute
import com.example.android_app.presentation.company.home.CompanyHomeTopBar
import com.example.android_app.presentation.company.jobposting.JobPostingScreen
import com.example.android_app.presentation.company.signup.CompanySignUpScreen
import com.example.android_app.presentation.company.applicationManagement.AppManagementViewModel
import com.example.android_app.presentation.authentication.roleSelection.RoleSelectionScreen
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
enum class UserType {
    APPLICANT,
    COMPANY
}
// 1. Define your Routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ApplicantSignUp : Screen("ApplicantSignUp")
    object Home : Screen("home")
    object JobDetails : Screen("job_details")
    object ApplicationManagement : Screen("application_management")
    object SavedJobs : Screen("saved_jobs")
    object CompanyHome : Screen("company_home")
    object CompanySignUp : Screen("company_signup")
    object JobPosting : Screen("job_posting")
    object CompanyManagement : Screen("company_management")
    object JobPostingDetails : Screen("job_posting_details")
    object RoleSelection : Screen("role_selection")

}

@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    // We set 'signup' as start destination for testing
    startDestination: String = Screen.Login.route
) {
    // --- PERSISTENT STORAGE with Room Database ---
    val context = LocalContext.current
    val sharedRepository = remember {
        val database = AppDatabase.getDatabase(context)
        val dao = database.savedJobDao()
        SavedJobRepository(dao = dao, firebase = FirebaseFirestore.getInstance())
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        // --- Role Selection Screen ---
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onSelectApplicant = {
                    navController.navigate(Screen.ApplicantSignUp.route)
                },
                onSelectCompany = {
                    navController.navigate(Screen.CompanySignUp.route)
                }
            )
        }
        // --- Login Screen ---
        composable(Screen.Login.route) {
            // Placeholder for your actual Login Screen
                LoginScreen(
                    onNavigateFromSignUp = {
                        navController.navigate(Screen.RoleSelection.route)
                    },
                    onLoginSuccess = { userType ->

                        // Route to the correct home screen based on UserType
                        val destination = when (userType) {
                            UserType.APPLICANT -> "applicant_graph" // Start the flow
                            UserType.COMPANY -> Screen.CompanyHome.route
                        }

                        // Navigate and clear Login from the back stack
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
        }
        // ----------------------------- Applicant Navigation -----------------------------

        // --- Applicant Sign Up Screen ---
        composable(Screen.ApplicantSignUp.route) {
            ApplicantSignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ApplicantSignUp.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Applicant Graph (Home -> Details -> Saved) ---
        // Using a route for the graph to scope the ViewModel
        navigation(
            startDestination = Screen.ApplicationManagement.route,
            route = "applicant_graph"
        ) {
            
            // --- Applicant Home Screen ---
            composable(Screen.ApplicationManagement.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("applicant_graph") }
                val viewModel: ApplicantHomeViewModel = viewModel(
                    parentEntry,
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ApplicantHomeViewModel(savedJobRepository = sharedRepository) as T
                        }
                    }
                )
                val state by viewModel.state.collectAsState()

                ApplicantHomeScreen(
                    jobs = state.jobs,
                    errorMessage = state.errorMessage,
                    isLoading = state.isLoading,
                    onLogoutClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onSavedJobsClick = {
                        navController.navigate(Screen.SavedJobs.route)
                    },
                    onViewDetailsClick = { job ->
                        // Navigate with companyId and jobId as arguments
                        navController.navigate("job_details/${job.companyId}/${job.id}")
                    },
                    onSaveClick = { job ->
                        viewModel.onSaveJob(job)
                    },
                    savedJobIds = state.savedJobIds
                )
            }
            
            // --- Applicant Job Details Screen ---
            composable(
                route = "job_details/{companyId}/{jobId}",
                arguments = listOf(
                    androidx.navigation.navArgument("companyId") { type = androidx.navigation.NavType.StringType },
                    androidx.navigation.navArgument("jobId") { type = androidx.navigation.NavType.StringType }
                )
            ) { entry ->
                // Create JobDetailsViewModel with navigation arguments
                val viewModel: com.example.android_app.presentation.applicant.jobdetails.JobDetailsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return com.example.android_app.presentation.applicant.jobdetails.JobDetailsViewModel(
                                savedStateHandle = entry.toSavedStateHandle(),
                                savedJobRepository = sharedRepository
                            ) as T
                        }
                    }
                )
                
                val state by viewModel.state.collectAsState()
                val context = androidx.compose.ui.platform.LocalContext.current
                
                // Show loading or error states
                when {
                    state.isLoading -> {
                        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    state.error != null -> {
                        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: ${state.error}", color = androidx.compose.ui.graphics.Color.Red)
                                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                                Button(onClick = { navController.popBackStack() }) {
                                    Text("Go Back")
                                }
                            }
                        }
                    }
                    state.job != null -> {
                        JobPostingDetailsScreen(
                            job = state.job!!,
                            isSaved = state.isSaved, // Pass saved status to update button
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onApplyClick = {
                                viewModel.applyToJob(
                                    onSuccess = {
                                        android.widget.Toast.makeText(context, "Application Submitted Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onFailure = { error ->
                                        android.widget.Toast.makeText(context, "Failed: $error", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            onSaveClick = {
                                viewModel.toggleSaveJob()
                                val message = if (state.isSaved) "Job Removed from Saved" else "Job Saved"
                                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // --- Saved Jobs Screen ---
            composable(Screen.SavedJobs.route) { entry ->

                val context = LocalContext.current
                
                val viewModel: savedJobsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                             return savedJobsViewModel(
                                 repository = sharedRepository
                             ) as T
                        }
                    }
                )

                val uiState by viewModel.uiState.collectAsState()

                SavedJobsScreen(
                    uiState = uiState,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onApplyClick = { savedJob ->
                         viewModel.applyJob(savedJob) { jobId ->
                             // Navigate to details by finding it or fetching?
                             // Since details screen expects state from ApplicantHomeViewModel (shared),
                             // we might need to navigate there. Use route with ID is better but current impl uses shared VM.
                             // For now, simple pop back or nav to details if supported?
                             // The existing graph expects ApplicantHomeViewModel to have selected job.
                             // We might just pop back to home and let them find it? OR:
                             navController.navigate(Screen.Home.route) // Simplify for now
                         }
                    },
                    onRemoveClick = { savedJob ->
                        viewModel.removeJob(savedJob)
                    }
                )


            }
        }

// ----------------------------- Company Navigation -----------------------------

        // --- Company Sign Up Screen ---
        composable(Screen.CompanySignUp.route) {
            // Since ApplicantSignUpScreen handles the ViewModel internally,
            // we only need to pass the navigation callback.
            CompanySignUpScreen(
                onNavigateToLogin = {
                    // Navigate to Login and clear the backstack
                    // (so the user can't click "Back" to return to the signup page)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.CompanySignUp.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Company Home Screen ---
        composable(Screen.CompanyHome.route) {
            CompanyHomeRoute(
                onLogout = {
                    // Logic is now handled inside CompanyHomeRoute (viewModel.logout())
                    // Here we just handle navigation
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.CompanyHome.route) { inclusive = true }
                    }
                },
                onAddJob = {
                    navController.navigate(Screen.JobPosting.route)
                },
                onViewApplicants = { jobId ->
                    navController.navigate("${Screen.CompanyManagement.route}/$jobId")                }
            )
        }

        // --- Job Posting Screen ---
        composable(Screen.JobPosting.route){
            JobPostingScreen(
                onBack = {
                    navController.popBackStack()
                },
                onDiscard = {
                    navController.popBackStack()
                },
                onPost = {
                    navController.popBackStack()
                }
            )
        }

        // --- View Applicants Screen ---
        composable(
            route = "${Screen.CompanyManagement.route}/{jobId}",
            arguments = listOf(androidx.navigation.navArgument("jobId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            // CHANGED: Extract the jobId from the navigation arguments
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""

            val viewModel: AppManagementViewModel = viewModel()

            // CHANGED: Trigger the fetch when this screen opens
            LaunchedEffect(jobId) {
                viewModel.fetchApplicants(jobId)
            }

            val state by viewModel.uiState.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current

            ApplicationManagementScreen(
                applicants = state.applicants,
                onBack = {
                    navController.popBackStack()
                },
                onViewResume = { resumeUrl ->
                    if (!resumeUrl.isNullOrBlank()) {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(resumeUrl))
                        val chooser = android.content.Intent.createChooser(intent, "Open Resume with")
                        context.startActivity(chooser)
                    } else {
                        android.widget.Toast.makeText(context, "No resume link available", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                onStatusChange = { applicationId, newStatus ->
                    // Call ViewModel to update status in Firebase (following MVVM pattern)
                    viewModel.updateApplicationStatus(applicationId, newStatus)
                    android.widget.Toast.makeText(context, "Status updated to: $newStatus", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }


    }

}

// Extension function to convert NavBackStackEntry to SavedStateHandle
fun androidx.navigation.NavBackStackEntry.toSavedStateHandle(): SavedStateHandle {
    val handle = SavedStateHandle()
    arguments?.let { bundle ->
        bundle.keySet().forEach { key ->
            handle[key] = bundle.get(key)
        }
    }
    return handle
}

// Temp placeholder if class doesn't exist
class TempDb { fun savedJobDao(): SavedJobDao? = null }