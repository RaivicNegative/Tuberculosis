package com.example.tuberculosispredictionapp.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tuberculosispredictionapp.PredictionViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HistoryScreen(modifier: Modifier, viewModel: PredictionViewModel = viewModel()) {
    // Get the current user ID (ensure the user is logged in)
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {

        LaunchedEffect(userId) {
            viewModel.fetchSymptomsFromDatabase(userId)
        }
    } else {
        Log.e("HistoryScreen", "User not authenticated.")
    }

    val symptomsHistory = viewModel.symptomsHistory.collectAsState().value

    Log.d("HistoryScreen", "Symptoms: $symptomsHistory")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Symptom History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (symptomsHistory.isEmpty()) {
            Text("No symptoms found.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Use items to display the list of symptoms
                items(symptomsHistory) { symptomEntry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = symptomEntry.symptom,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Timestamp: ${symptomEntry.timestamp}",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

