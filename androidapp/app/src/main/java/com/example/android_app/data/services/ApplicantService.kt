package com.example.android_app.data.services

import com.example.android_app.data.models.Applicant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ApplicantService {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("Applicant")

    suspend fun createApplicant(applicant: Applicant): Result<Unit> {
        return try {
            collection.document(applicant.id).set(applicant.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getApplicant(id: String): Result<Applicant?> {
        return try {
            val snapshot = collection.document(id).get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Applicant.fromFirestore(snapshot.id, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
