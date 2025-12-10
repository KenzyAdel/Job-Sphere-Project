import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_app.presentation.applicant.home.ApplicantHomeScreen
import com.example.android_app.presentation.applicant.signup.ApplicantSignUpScreen
import com.example.android_app.presentation.authentication.login.LoginScreen
import com.example.android_app.presentation.authentication.login.UserRole
import com.example.android_app.presentation.authentication.login.loginViewModel
import com.example.android_app.presentation.authentication.roleSelection.RoleSelectionScreen
import com.example.android_app.presentation.company.home.CompanyHomeScreen
import com.example.android_app.presentation.company.signup.CompanySignUpScreen
import com.example.android_app.presentation.navigation.Screen

// Import your specific screens here

@Composable
fun JobSphereNavGraph(
    // In a real app, you might inject the ViewModel via Hilt/Koin inside the composable
    loginViewModel: loginViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {

        // --- LOGIN SCREEN ---
        composable<Screen.Login> {
            val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.loginSuccessRole) {
                uiState.loginSuccessRole?.let { role ->
                    val destination = when (role) {
                        UserRole.COMPANY -> Screen.CompanyHome
                        UserRole.APPLICANT -> Screen.ApplicantHome
                    }

                    // Navigate and CLEAR BACK STACK so user can't go back to login
                    navController.navigate(destination) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            // Render the actual Login UI
            LoginScreen(
                uiState = uiState,
                onLoginClick = { email, pass ->
                    // This triggers the Firebase check in your ViewModel
                    loginViewModel.login(email, pass)
                },
                onSignUpClick = {
                    // Navigate to Role Selection
                    navController.navigate(Screen.RoleSelection)
                }
            )
        }

        // --- ROLE SELECTION SCREEN ---
        composable<Screen.RoleSelection> {
            RoleSelectionScreen(
                onApplicantSelected = { navController.navigate(Screen.ApplicantSignUp) },
                onCompanySelected = { navController.navigate(Screen.CompanySignUp) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- SIGN UP SCREENS ---
        composable<Screen.ApplicantSignUp> {
            ApplicantSignUpScreen(
                onSignUpSuccess = {
                    // Navigate to Home and clear backstack (Auth flow complete)
                    navController.navigate(Screen.ApplicantHome) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.CompanySignUp> {
            CompanySignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.CompanyHome) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        // --- HOME SCREENS ---
//        composable<Screen.ApplicantHome> {
//            ApplicantHomeScreen()
//        }
//
//        composable<Screen.CompanyHome> {
//            CompanyHomeScreen()
//        }
    }
}