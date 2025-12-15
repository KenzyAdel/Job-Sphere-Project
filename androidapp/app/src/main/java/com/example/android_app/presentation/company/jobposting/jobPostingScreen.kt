package com.example.android_app.presentation.company.jobposting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ----------------------
// TOP BAR
// ----------------------
@Composable
fun JobPostingTopBar(onBack: () -> Unit = {}) {
    val topBarColor = Color(0xFF7B1FA2) // Purple
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(topBarColor)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Job Details",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ----------------------
// JOB POSTING SCREEN
// ----------------------
@Composable
fun JobPostingScreen(
    viewModel: JobPostingViewModel = viewModel(),
    onBack: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onPost: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Handle success navigation
    LaunchedEffect(state.isPostSuccess) {
        if (state.isPostSuccess) {
            onPost()
            viewModel.resetState()
        }
    }

    // Job Type dropdown state
    var expanded by remember { mutableStateOf(false) }
    val jobTypes = listOf("Full Time", "Part Time", "Intern", "Remote")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        JobPostingTopBar(onBack = onBack)

        Spacer(Modifier.height(16.dp))
        
        // Error Message Display
        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }

        // TextFields
        JobTextField(
            label = "Job Title", 
            value = state.title, 
            onValueChange = viewModel::onTitleChange
        )
        Spacer(Modifier.height(12.dp))
        
        JobTextField(
            label = "Location", 
            value = state.location, 
            onValueChange = viewModel::onLocationChange
        )
        Spacer(Modifier.height(12.dp))
        
        JobTextField(
            label = "Salary (Optional)", 
            value = state.salary, 
            onValueChange = viewModel::onSalaryChange
        )
        Spacer(Modifier.height(12.dp))
        
        JobTextField(
            label = "Description", 
            value = state.description, 
            onValueChange = viewModel::onDescriptionChange, 
            singleLine = false
        )
        Spacer(Modifier.height(12.dp))
        
        JobTextField(
            label = "Responsibilities", 
            value = state.responsibilities, 
            onValueChange = viewModel::onResponsibilitiesChange, 
            singleLine = false
        )
        Spacer(Modifier.height(12.dp))
        
        JobTextField(
            label = "Requirements", 
            value = state.requirements, 
            onValueChange = viewModel::onRequirementsChange, 
            singleLine = false
        )
        Spacer(Modifier.height(12.dp))

        // Job Type Dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Job Type", fontSize = 16.sp, fontWeight = FontWeight.Medium)

            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(state.selectedJobType, color = Color.White)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    jobTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onJobTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDiscard,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Discard", color = Color.White)
            }

            Button(
                onClick = viewModel::onPostJob,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White, 
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Post", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ----------------------
// REUSABLE TEXTFIELD
// ----------------------
@Composable
fun JobTextField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth(),
        minLines = if (singleLine) 1 else 3
    )
}

// ----------------------
// PREVIEW
// ----------------------
@Preview(showBackground = true)
@Composable
fun JobPostingScreenPreview() {
    JobPostingScreen()
}
