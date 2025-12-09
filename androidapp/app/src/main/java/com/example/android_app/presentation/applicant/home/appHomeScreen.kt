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
    onLogoutClick: () -> Unit = {},
    onViewDetailsClick: (JobItem) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Filter by") }
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
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onLogoutClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA))) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))



        Spacer(modifier = Modifier.height(10.dp))

        // Filter Dropdown
        var expanded by remember { mutableStateOf(false) }


        Spacer(modifier = Modifier.height(16.dp))

        // Job List or Empty Message
        if (jobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No available jobs right now",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(jobs) { job ->
                    JobCard(job = job, onViewDetailsClick = onViewDetailsClick)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun JobCard(job: JobItem, onViewDetailsClick: (JobItem) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = job.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = job.company, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { onViewDetailsClick(job) }) {
                Text("View Details")
            }
        }
    }
}

// Data Model

data class JobItem(val title: String, val company: String)

@Preview(showBackground = true)
@Composable
fun ApplicantHomeScreenPreview() {
    val sampleJobs = listOf(
        JobItem("Android Developer", "Google"),
        JobItem("Backend Engineer", "Amazon"),
        JobItem("UI/UX Designer", "Meta")
    )
    ApplicantHomeScreen(jobs = sampleJobs)
}
