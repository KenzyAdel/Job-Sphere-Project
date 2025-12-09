package com.example.android_app.data.DAOs
import androidx.room.*
import com.example.android_app.data.entities.companyEntity

@Dao
interface companyDao {



    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerCompany(company: companyEntity)

    @Query("SELECT * FROM companies WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): companyEntity?

    @Update
    suspend fun updateCompany(company: companyEntity)

    @Query("SELECT * FROM companies WHERE id = :id")
    suspend fun getCompany(id: Int): companyEntity?
}
