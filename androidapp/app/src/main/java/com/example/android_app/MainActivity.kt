package com.example.android_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.android_app.presentation.company.signup.CompanySignUpScreen
import com.example.android_app.presentation.applicant.signup.ApplicantSignUpScreen// 1. Import the sign-up screen
import com.example.android_app.presentation.authentication.login.LoginScreen
import com.example.android_app.presentation.navigation.AppNavigation
import com.example.android_app.ui.theme.AndroidappTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is good for initial testing, you can keep or remove it
        try {
            Firebase.auth
            Log.d("FirebaseTest", "✅ Firebase initialized successfully!")
        } catch (e: Exception) {
            Log.e("FirebaseTest", "❌ Firebase initialization failed: ${e.message}")
        }

        enableEdgeToEdge()
        setContent {
            AndroidappTheme {
                // Use a Surface for better background color handling
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 2. Set your companySignUpScreen as the content
                   // LoginScreen()

                    AppNavigation()
                }
            }
        }
    }
}

