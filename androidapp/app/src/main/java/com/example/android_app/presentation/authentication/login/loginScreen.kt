package com.example.android_app.presentation.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.ImeAction

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit, // CHANGED: Now accepts UserRole parameter
    onNavigateToSignUp: () -> Unit = {},
    viewModel: loginViewModel = viewModel()
) {
    // Collect the UiState from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // --- NEW: Handle login success with role ---
    LaunchedEffect(key1 = uiState.loginSuccessRole) {
        uiState.loginSuccessRole?.let { role ->
            onLoginSuccess(role) // Navigate with the role
            // Reset after navigation
            viewModel.onLoginSuccessNavigated()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

// Private Composable to handle the UI drawing based on the state
@Composable
private fun LoginContent(
    uiState: loginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    // Local UI state for password visibility
    var passwordVisible by remember { mutableStateOf(false) }

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Email Field ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Password Field ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                // visibility toggle
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.loginError != null) {
                Text(
                    text = uiState.loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Login Button ---
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading &&
                        uiState.email.isNotBlank() &&
                        uiState.password.isNotBlank() // Optional: Enable only when fields are filled
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Sign Up Text Button ---
            TextButton(
                onClick = onNavigateToSignUp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? Sign up",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = loginUiState(
            email = "preview@example.com",
            password = "123"
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onNavigateToSignUp = {}
    )
}