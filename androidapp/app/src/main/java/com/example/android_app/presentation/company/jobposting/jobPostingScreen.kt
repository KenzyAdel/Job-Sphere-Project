package com.example.android_app.presentation.company.jobposting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.shape.RoundedCornerShape

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
    onPause: () -> Unit = {},
    onDiscard: () -> Unit = {},
    onPost: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var responsibilities by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }

    // Job Type dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedJobType by remember { mutableStateOf("Full Time") }
    val jobTypes = listOf("Full Time", "Part Time", "Intern", "Remote")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        JobPostingTopBar()

        Spacer(Modifier.height(16.dp))

        // TextFields
        JobTextField(label = "Job Title", value = title, onValueChange = { title = it })
        Spacer(Modifier.height(12.dp))
        JobTextField(label = "Location", value = location, onValueChange = { location = it })
        Spacer(Modifier.height(12.dp))
        JobTextField(label = "Salary (Optional)", value = salary, onValueChange = { salary = it })
        Spacer(Modifier.height(12.dp))
        JobTextField(label = "Description", value = description, onValueChange = { description = it }, singleLine = false)
        Spacer(Modifier.height(12.dp))
        JobTextField(label = "Responsibilities", value = responsibilities, onValueChange = { responsibilities = it }, singleLine = false)
        Spacer(Modifier.height(12.dp))
        JobTextField(label = "Requirements", value = requirements, onValueChange = { requirements = it }, singleLine = false)
        Spacer(Modifier.height(12.dp))

        // Job Type in the same row
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
                    Text(selectedJobType, color = Color.White)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    jobTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedJobType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Buttons: Pause, Discard, Post
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
                onClick = onPost,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
            ) {
                Text("Post", color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ----------------------
// REUSABLE TEXTFIELD
// ----------------------
@Composable
fun JobTextField(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth()
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
