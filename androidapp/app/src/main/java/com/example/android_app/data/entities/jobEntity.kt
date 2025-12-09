package com.example.android_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class jobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Relation
    val companyId: Int,   // FK to CompanyEntity

    // Mandatory fields
    val title: String,
    val location: String,
    val description: String,
    val responsibilities: String,
    val requirements: String,
    val jobType: String,   // "Full Time", "Part Time", "Remote", "Intern"

    // Optional
    val salary: String? = null
)
