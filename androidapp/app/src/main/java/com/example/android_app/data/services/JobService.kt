package com.example.android_app.data.services

import com.example.android_app.data.models.Job
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class JobService {
    private val db = FirebaseFirestore.getInstance()

    private fun getJobsCollection(companyId: String) =
        db.collection("Company").document(companyId).collection("Jobs")

    suspend fun createJob(companyId: String, job: Job): Result<String> {
        return try {
            val docRef = getJobsCollection(companyId).add(job.toHashMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getJob(companyId: String, jobId: String): Result<Job?> {
        return try {
            val snapshot = getJobsCollection(companyId).document(jobId).get().await()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return Result.success(null)
                Result.success(Job.fromFirestore(snapshot.id, companyId, data))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllJobsForCompany(companyId: String): Result<List<Job>> {
        return try {
            val snapshot = getJobsCollection(companyId).get().await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllJobs(): Result<List<Job>> {
        return try {
            val snapshot = db.collectionGroup("Jobs").get().await()
            val jobs = snapshot.documents.mapNotNull { doc ->
                val companyId = doc.reference.parent.parent?.id
                if (companyId != null) {
                    doc.data?.let { Job.fromFirestore(doc.id, companyId, it) }
                } else {
                    null
                }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJob(companyId: String, jobId: String): Result<Unit> {
        return try {
            getJobsCollection(companyId).document(jobId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
