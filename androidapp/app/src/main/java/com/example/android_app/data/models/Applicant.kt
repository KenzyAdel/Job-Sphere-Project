package com.example.android_app.data.models

data class Applicant(
    val id: String = "",
    val cvLink: String = "",
    val email: String = "",
    val linkedin: String = "",
    val phone: String = "",
    val name: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "CV-Link" to cvLink,
            "Email" to email,
            "Linkedin" to linkedin,
            "Phone" to phone,
            "Name" to name
        )
    }

    companion object {
        fun fromFirestore(id: String, data: Map<String, Any>): Applicant {
            return Applicant(
                id = id,
                cvLink = data["CV-Link"] as? String ?: "",
                email = data["Email"] as? String ?: "",
                linkedin = data["Linkedin"] as? String ?: "",
                phone = data["Phone"] as? String ?: "",
                name = data["Name"] as? String ?: ""
            )
        }
    }
}
