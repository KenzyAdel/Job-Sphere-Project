package com.example.android_app.presentation.company.signup

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
fun CompanySignUpScreen(
    onNavigateToLogin: () -> Unit = {},
    // Inject ViewModel
    viewModel: companySignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Side Effect Handling ---
    LaunchedEffect(key1 = uiState.isSignUpSuccess) {
        if (uiState.isSignUpSuccess) {
            onNavigateToLogin()
        }
    }

    CompanySignUpContent(
        uiState = uiState,
        onCompanyNameChange = viewModel::onCompanyNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onWebsiteChange = viewModel::onWebsiteChange,
        onPhoneChange = viewModel::onPhoneChange,
        onIndustryChange = viewModel::onIndustryChange,
        onLinkedInChange = viewModel::onLinkedInChange,
        onSignUpClick = viewModel::onSignUpClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
private fun CompanySignUpContent(
    uiState: companySignUpUiState,
    onCompanyNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onIndustryChange: (String) -> Unit,
    onLinkedInChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // Added scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Company Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Company Name ---
            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = onCompanyNameChange,
                label = { Text("Company Name") },
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

            Spacer(modifier = Modifier.height(12.dp))

            // --- Optional fields ---
            OutlinedTextField(
                value = uiState.website,
                onValueChange = onWebsiteChange,
                label = { Text("Website (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

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
                value = uiState.industry,
                onValueChange = onIndustryChange,
                label = { Text("Industry (Optional)") },
                singleLine = true,
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
                enabled = !uiState.isLoading,
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
fun CompanySignUpScreenPreview() {
    MaterialTheme {
        // Preview content directly to avoid ViewModel crash
        CompanySignUpContent(
            uiState = companySignUpUiState(),
            onCompanyNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onWebsiteChange = {},
            onPhoneChange = {},
            onIndustryChange = {},
            onLinkedInChange = {},
            onSignUpClick = {},
            onNavigateToLogin = {}
        )
    }
}