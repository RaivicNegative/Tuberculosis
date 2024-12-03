package com.example.tuberculosispredictionapp.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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

@Composable
fun History(modifier: Modifier = Modifier, viewModel: PredictionViewModel = viewModel()) {
    // Collect the symptoms history as a List of SymptomEntry from the ViewModel's StateFlow
    val symptomsHistory = viewModel.symptomsHistory.collectAsState().value

    // Fetch symptoms from the database when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchSymptomsFromDatabase(userId = "12345")  // Replace with actual userId
    }

    // Log to verify symptoms history
    Log.d("History", "Symptoms: $symptomsHistory")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Symptom History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Check if there are no symptoms to show
        if (symptomsHistory.isEmpty()) {
            Text("No symptoms found.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Use items to display the list of symptoms
                items(symptomsHistory) { symptomEntry ->
                    SymptomItem(symptomEntry)
                }
            }
        }
    }
}

@Composable
fun SymptomItem(symptomEntry: SymptomEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        elevation = 4.dp
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

fun Card(
    modifier: Modifier,
    shape: RoundedCornerShape,
    elevation: Dp,
    function: @Composable ColumnScope.() -> Unit
) {
}
