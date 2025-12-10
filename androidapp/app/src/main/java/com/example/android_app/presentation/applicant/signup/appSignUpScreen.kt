package com.example.android_app.presentation.applicant.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ApplicantSignUpScreen(
    onNavigateToLogin: () -> Unit = {},
    // Inject the ViewModel
    viewModel: AppSignUpViewModel = viewModel()
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // --- Side Effect Handling ---
    // If sign up is successful, navigate to the next screen
    LaunchedEffect(key1 = uiState.isSignUpSuccess) {
        if (uiState.isSignUpSuccess) {
            onNavigateToLogin()
        }
    }

    ApplicantSignUpContent(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onPhoneChange = viewModel::onPhoneChange,
        onLinkedInChange = viewModel::onLinkedInChange,
        onCVLinkChange = viewModel::onCVLinkChange,
        onSignUpClick = viewModel::onSignUpClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun ApplicantSignUpContent(
    uiState: AppSignUpUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLinkedInChange: (String) -> Unit,
    onCVLinkChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        // Added verticalScroll to handle smaller screens or landscape mode
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Applicant Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Full Name ---
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Email ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Password ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Confirm Password ---
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // --- Optional Fields ---
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone (Optional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.linkedIn,
                onValueChange = onLinkedInChange,
                label = { Text("LinkedIn (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.CVLink,
                onValueChange = onCVLinkChange,
                label = { Text("CV Link") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- Error Message ---
            if (uiState.signUpError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.signUpError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Sign Up Button ---
            Button(
                onClick = onSignUpClick,
                enabled = !uiState.isLoading, // Disable while loading
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ApplicantSignUpScreenPreview() {
    MaterialTheme {
        ApplicantSignUpContent(
            uiState = AppSignUpUiState(fullName = "John Doe"),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onPhoneChange = {},
            onLinkedInChange = {},
            onCVLinkChange = {},
            onSignUpClick = {},
            onNavigateToLogin = {}
        )
    }
}