package com.example.android_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class companyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Mandatory
    val name: String,
    val email: String,
    val password: String,

    // Optional
    val website: String? = null,
    val industry: String? = null,
    val linkedin: String? = null,
    val phone: String? = null
    )
