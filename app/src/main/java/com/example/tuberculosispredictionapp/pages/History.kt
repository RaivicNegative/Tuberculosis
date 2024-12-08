package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tuberculosispredictionapp.PredictionViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: PredictionViewModel = viewModel()
) {
    val combinedHistory by viewModel.combinedHistory.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val outputDateFormat = remember {
        java.text.SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Prediction History", fontSize = 30.sp,
                    style = TextStyle(fontFamily = customRobotoFontFamily,
                        fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF81C784)),
                actions = {}
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEDF7FB))
                    .padding(paddingValues)
            ) {
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(550.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF59D)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {

                            if (combinedHistory.isEmpty()) {
                                Text(
                                    text = "No history found.",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = customRobotoFontFamily
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(top = 16.dp)
                                )
                            } else {

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    items(combinedHistory.asReversed()) { entry ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFA5D6A7)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {

                                                val philippineTime = remember(entry.timestamp) {
                                                    try {

                                                        val parsedDate = java.text.SimpleDateFormat(
                                                            "EEEE, MMMM d, yyyy h:mm a",
                                                            Locale.getDefault()
                                                        ).apply {
                                                            timeZone = java.util.TimeZone.getTimeZone("Asia/Manila")
                                                        }.parse(entry.timestamp)


                                                        parsedDate?.let {
                                                            outputDateFormat.format(it)
                                                        } ?: entry.timestamp
                                                    } catch (e: Exception) {
                                                        entry.timestamp
                                                    }
                                                }

                                                Text(
                                                    text = "Timestamp: $philippineTime",
                                                    fontSize = 14.sp,
                                                    style = TextStyle(
                                                        fontFamily = customRobotoFontFamily,
                                                        fontStyle = FontStyle.Italic
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = "Symptoms:",
                                                    fontSize = 16.sp,
                                                    style = TextStyle(
                                                        fontFamily = customRobotoFontFamily,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                )
                                                entry.symptoms.forEach { symptom ->
                                                    Text(
                                                        text = "- ${symptom.symptom}",
                                                        fontSize = 14.sp,
                                                        style = TextStyle(fontFamily = customRobotoFontFamily)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Prediction: ${entry.prediction}",
                                                    fontSize = 16.sp,
                                                    style = TextStyle(
                                                        fontFamily = customRobotoFontFamily,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text(
                                                    text = "Confidence: ${(entry.confidence * 100).toInt()}%",
                                                    fontSize = 14.sp,
                                                    style = TextStyle(
                                                        fontFamily = customRobotoFontFamily,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text(
                                                    text = "Risk Category: ${entry.riskCategory}",
                                                    fontSize = 14.sp,
                                                    style = TextStyle(
                                                        fontFamily = customRobotoFontFamily,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(
                            onClick = {
                                scope.launch {
                                    viewModel.clearHistory(
                                        onSuccess = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("History cleared successfully.")
                                            }
                                        },
                                        onFailure = { error ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Failed to clear history: ${error.message}")
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                        ) {
                            Text("Clear History", style = TextStyle(fontFamily = customRobotoFontFamily), color = Color(0xFF1565C0))
                        }
                    }
                }
            }
        }
    )
}







