package com.example.android_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.android_app.presentation.company.signup.CompanySignUpScreen
import com.example.android_app.presentation.applicant.signup.ApplicantSignUpScreen// 1. Import the sign-up screen
import com.example.android_app.presentation.authentication.login.LoginScreen
import com.example.android_app.presentation.navigation.AppNavigationGraph
import com.example.android_app.ui.theme.AndroidappTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // wrap this in your theme if you have one, e.g. AndroidAppTheme { ... }

            // 1. Create the NavController
            val navController = rememberNavController()

            // 2. Call your Navigation Graph
            AppNavigationGraph(navController = navController)
        }
    }
}
