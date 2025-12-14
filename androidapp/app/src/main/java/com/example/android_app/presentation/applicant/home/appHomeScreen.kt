package com.example.android_app.presentation.applicant.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ApplicantHomeScreen(
    jobs: List<JobItem>,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onLogoutClick: () -> Unit = {},
    onSavedJobsClick: () -> Unit = {}, // 1. Added new callback parameter
    onViewDetailsClick: (JobItem) -> Unit = {},
    onSaveClick: (JobItem) -> Unit = {},
    savedJobIds: Set<String> = emptySet() // Track which jobs are saved
) {
    var selectedFilter by remember { mutableStateOf("Filter by") }
    // Filters logic (placeholder)
    val filters = listOf("Full time", "Part time", "Remote", "Intern")

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A1B9A))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Find Your Job",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f) // Text takes available space
            )

            // 2. Added a Row to group the buttons (Saved & Logout)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Saved Jobs Button
                Button(
                    onClick = onSavedJobsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA68C8)) // Lighter purple
                ) {
                    Text("Saved Jobs")
                }

                // Logout Button
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA))
                ) {
                    Text("Logout")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Job List, Error, or Empty Message
        if (isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No available jobs right now",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(jobs) { job ->
                    JobCard(
                        job = job,
                        onViewDetailsClick = onViewDetailsClick,
                        onSaveClick = onSaveClick,
                        isSaved = savedJobIds.contains(job.id)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: JobItem,
    onViewDetailsClick: (JobItem) -> Unit,
    onSaveClick: (JobItem) -> Unit,
    isSaved: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = job.company,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onViewDetailsClick(job) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }

                Button(
                    onClick = { 
                        println("🔘 SAVE BUTTON CLICKED - Job: ${job.title}, ID: ${job.id}")
                        onSaveClick(job) 
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) Color(0xFFE53935) else Color(0xFF4CAF50)
                    )
                ) {
                    Text(if (isSaved) "Unsave" else "Save")
                }
            }
        }
    }
}

// Data Model (Note: Ensure this doesn't conflict if you moved it to appHomeUiState.kt)
data class JobItem(
    val id: String = "",
    val companyId: String = "", // Added for navigation to job details
    val title: String="",
    val company: String="",
    val location: String = "",
    val salary: String = "",
    val description: String = "",
    val responsibilities: String = "",
    val requirements: String = ""
)

@Preview(showBackground = true)
@Composable
fun ApplicantHomeScreenPreview() {
    MaterialTheme {
        val sampleJobs = listOf(
            JobItem(
                id = "1",
                title = "Android Developer",
                company = "Google",
                location = "Mountain View, CA",
                salary = "$120,000 - $180,000"
            )
        )
        ApplicantHomeScreen(
            jobs = sampleJobs,
            onLogoutClick = {},
            onSavedJobsClick = {}, // Preview support
            onViewDetailsClick = {},
            onSaveClick = {}
        )
    }
}