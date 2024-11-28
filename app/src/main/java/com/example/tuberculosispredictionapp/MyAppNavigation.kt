package com.example.tuberculosispredictionapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tuberculosispredictionapp.pages.*

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login") {

        composable("Login") {
            LoginPage(modifier, navController, authViewModel)
        }

        composable("register") {
            RegisterPage(modifier, navController, authViewModel)
        }

        composable("main") {
            MainScreen(modifier, navController, authViewModel, profileViewModel)
        }

        composable("predict") {
            val predictionViewModel: PredictionViewModel = viewModel()
            PredictTuberculosis(navController, predictionViewModel)
        }

        composable("what") {
            WhatIsTuberculosis(navController)
        }

        composable("treatment") {
            TreatmentGuide(navController)
        }

        composable(
            route = "recommendation/{hasPredicted}",
            arguments = listOf(navArgument("hasPredicted") { type = NavType.BoolType })
        ) { backStackEntry ->
            val hasPredicted = backStackEntry.arguments?.getBoolean("hasPredicted") ?: false
            Recommendation(navController, hasPredicted)
        }

        composable(
            "result/{disease}/{confidence}/{riskCategory}",
            arguments = listOf(
                navArgument("disease") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.IntType },
                navArgument("riskCategory") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val disease = backStackEntry.arguments?.getString("disease") ?: "Unknown"
            val confidence = backStackEntry.arguments?.getInt("confidence") ?: 0
            val riskCategory = backStackEntry.arguments?.getString("riskCategory") ?: "Unknown"
            Result(navController, disease, confidence, riskCategory)
        }

        composable("profile") {
            ProfilePage(modifier, profileViewModel, authViewModel)
        }
    }
}
