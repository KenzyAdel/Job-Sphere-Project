package com.example.android_app.presentation.company.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ----------------------------
// TOP BAR with Back Button
// ----------------------------
@Composable
fun CompanyHomeTopBar(
    onBack: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF7B1FD9))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Company Dashboard",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ----------------------------
// MAIN SCREEN
// ----------------------------
@Composable
fun CompanyHomeScreen(
    jobs: List<TempJobUiItem> = emptyList(),
    onBack: () -> Unit = {},
    onEditJob: (String) -> Unit = {},
    onToggleJobStatus: (String) -> Unit = {},
    onDeleteJob: (String) -> Unit = {},
    onViewApplicants: (String) -> Unit = {},
    onSearchCandidates: () -> Unit = {}, // Updated: Button callback instead of search query
    onAddJob: () -> Unit = {}
) {

    Scaffold(
        topBar = { CompanyHomeTopBar(onBack = onBack) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F4F4))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Spacer(Modifier.height(16.dp))

                // "Your Jobs" HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Jobs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (jobs.isEmpty()) {
                    // EMPTY STATE
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No jobs posted yet.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Create your first job posting to start receiving applications",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // JOB LIST
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(jobs) { job ->
                            JobCard(
                                job = job,
                                onEdit = onEditJob,
                                onToggleStatus = onToggleJobStatus,
                                onDelete = onDeleteJob,
                                onViewApplicants = onViewApplicants
                            )
                        }
                    }
                }
            }

            // ADD BUTTON - Square plus button in bottom right corner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Card(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7B1FD9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    IconButton(
                        onClick = onAddJob,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add new job",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------
// SINGLE JOB CARD
// ----------------------------
@Composable
fun JobCard(
    job: TempJobUiItem,
    onEdit: (String) -> Unit,
    onToggleStatus: (String) -> Unit,
    onDelete: (String) -> Unit,
    onViewApplicants: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row: Job title + dropdown menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(job.location, fontSize = 14.sp, color = Color.Gray)
                    job.salary?.let {
                        Spacer(Modifier.height(4.dp))
                        Text("Salary: $it", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Button(
                    onClick = { /* Handle delete action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)), // Use a distinct color for deletion
                    // Optional: Use a smaller size or an icon-only button for better fit
                ) {
                    Text("Delete", color = Color.White)
                    // You might consider an Icon for a more compact design:
                    // Icon(Icons.Filled.Delete, contentDescription = "Delete Job", tint = Color.White)
                }
            }

            Spacer(Modifier.height(12.dp))

            // View Applicants button (always visible)
            Button(
                onClick = { onViewApplicants(job.id) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Applicants", fontSize = 14.sp)
            }
        }
    }
}

// ----------------------------
// TEMP UI STRUCT FOR PREVIEW
// ----------------------------
class TempJobUiItem(
    val id: String,
    val title: String,
    val location: String,
    val salary: String?,
    val isActive: Boolean
)

// ----------------------------
// PREVIEW
// ----------------------------
@Preview(showBackground = true, widthDp = 360)
@Composable
fun CompanyHomeScreenPreview() {
    val sampleJobs = listOf(
        TempJobUiItem("1", "Android Developer", "Cairo", "20k - 30k EGP", true),
        TempJobUiItem("2", "UI/UX Designer", "Alexandria", null, false),
        TempJobUiItem("3", "Backend Engineer", "Remote", "25k - 40k EGP", true)
    )
    CompanyHomeScreen(
        jobs = sampleJobs,
        onAddJob = { /* Handle add job click */ },
        onSearchCandidates = { /* Handle search candidates click */ }
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun CompanyHomeScreenEmptyPreview() {
    CompanyHomeScreen(
        jobs = emptyList(),
        onAddJob = { /* Handle add job click */ },
        onSearchCandidates = { /* Handle search candidates click */ }
    )
}