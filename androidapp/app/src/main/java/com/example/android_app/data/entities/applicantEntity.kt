package com.example.android_app.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applicants")
data class applicantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val fullName: String,
    val email: String,

    // Mandatory for sign up
    val password: String,

    // Optional fields
    val linkedin: String? = null,
    val portfolio: String? = null,
    val phone: String? = null
    )
