package com.example.android_app.data.DAOs
import androidx.room.*
import com.example.android_app.data.entities.applicantEntity

@Dao
interface applicantDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerApplicant(applicant: applicantEntity)

    @Query("SELECT * FROM applicants WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): applicantEntity?

    @Update
    suspend fun updateApplicant(applicant: applicantEntity)

    @Query("SELECT * FROM applicants WHERE id = :id")
    suspend fun getApplicant(id: Int): applicantEntity?
}
