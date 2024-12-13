package com.example.tuberculosispredictionapp.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tuberculosispredictionapp.PredictionResult
import com.example.tuberculosispredictionapp.PredictionViewModel
import com.example.tuberculosispredictionapp.R
import kotlin.math.roundToInt

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictTuberculosis(navController: NavController, viewModel: PredictionViewModel = viewModel()) {
    var selectedSymptoms by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current
    val predictionResult by viewModel.predictionResult.observeAsState(PredictionResult())

    val userId = FirebaseAuth.getInstance().currentUser?.uid


    LaunchedEffect(userId) {
        viewModel.listenForPredictionUpdates(userId.toString())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Predict Tuberculosis",
                        fontSize = 30.sp,
                        style = TextStyle(
                            fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF81C784)
                ),
            )
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE1F5FE))
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp))
                {

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(1000.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFC3EFC5)),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Image(
                                        painter = painterResource(id = R.drawable.prediction),
                                        contentDescription = "Prediction Icon",
                                        modifier = Modifier.size(80.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Please click the box if you experience the symptoms below:",
                                        fontSize = 13.sp,
                                        style = TextStyle(fontFamily = customRobotoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(450.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {

                                val scrollState = rememberScrollState()
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(scrollState)
                                        .padding(start = 16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {

                                    Text(
                                        text = "*Main Symptoms:",
                                        fontSize = 16.sp,
                                        style = TextStyle(fontFamily = customRobotoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val symptoms = listOf(
                                        "Cough(>3weeks)",
                                        "Blood In Sputum",
                                        "Chest Pain",
                                        "Night Sweats",
                                        "Weight Loss",
                                        "Fever",
                                        "Fatigue",
                                        "Loss of Appetite"
                                    )

                                    symptoms.forEach { symptom ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = selectedSymptoms.contains(symptom),
                                                onCheckedChange = { isChecked ->
                                                    selectedSymptoms = if (isChecked) {
                                                        selectedSymptoms + symptom
                                                    } else {
                                                        selectedSymptoms - symptom
                                                    }
                                                }
                                            )
                                            Text(
                                                text = symptom,
                                                fontSize = 16.sp,
                                                style = TextStyle(fontFamily = customRobotoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "*Additional Symptoms (Select if applicable):",
                                        fontSize = 16.sp,
                                        style = TextStyle(fontFamily = customRobotoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            color = Color.Black
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val secondSetSymptoms = listOf(
                                        "Severe headache",
                                        "Back pain",
                                        "Joint pain",
                                        "Shortness of breath",
                                        "Nausea",
                                        "Vomiting",
                                        "Abdominal pain",
                                        "Sore throat",
                                        "Dizziness",
                                        "Muscle aches",
                                        "Sweating",
                                        "Skin rash",
                                        "Swollen lymph nodes",
                                        "Difficulty swallowing",
                                        "Shivering",
                                        "Palpitations",
                                        "Cold or chills",
                                        "Headache",
                                        "Nasal congestion",
                                        "Diarrhea",
                                        "Constipation",
                                        "Indigestion",
                                        "Heartburn",
                                        "Skin irritation or itching",
                                        "Joint swelling",
                                        "Mouth sores",
                                        "Trouble sleeping",
                                        "Difficulty breathing",
                                        "Memory loss or confusion",
                                        "Blurry vision",
                                        "Loss of taste or smell",
                                        "Cramps",
                                        "Excessive thirst or urination",
                                        "Unexplained bruising or bleeding"
                                    )

                                    secondSetSymptoms.forEach { symptom ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = selectedSymptoms.contains(symptom),
                                                onCheckedChange = { isChecked ->
                                                    selectedSymptoms = if (isChecked) {
                                                        selectedSymptoms + symptom
                                                    } else {
                                                        selectedSymptoms - symptom
                                                    }
                                                }
                                            )
                                            Text(
                                                text = symptom,
                                                fontSize = 16.sp,
                                                style = TextStyle(
                                                    fontFamily = customRobotoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        Log.d("Prediction", "Selected Symptoms: $selectedSymptoms")

                                        if (selectedSymptoms.isEmpty()) {
                                            Log.d("Prediction", "No symptoms selected.")

                                            Toast.makeText(context, "Please select at least one symptom", Toast.LENGTH_SHORT).show()

                                            viewModel.setHasPredicted(true)
                                        } else {

                                            viewModel.saveSymptomsToDatabase(
                                                userId.toString(),
                                                selectedSymptoms
                                            )


                                            val predictionResult = viewModel.getPredictionResult(context, selectedSymptoms)



                                            viewModel.setPredictionResult(predictionResult)
                                            viewModel.setHasPredicted(true)


                                            viewModel.savePrediction(
                                                userId.toString(),
                                                selectedSymptoms.map { it.hashCode() },
                                                predictionResult
                                            )


                                            val roundedConfidence = (predictionResult.confidence * 100).roundToInt()
                                            Log.d(
                                                "Prediction",
                                                "Disease: ${predictionResult.disease}, Confidence: $roundedConfidence%, Risk: ${predictionResult.riskCategory}"
                                            )


                                            navController.navigate(
                                                "result/${predictionResult.disease}/${
                                                    (predictionResult.confidence * 100).roundToInt()
                                                }/${predictionResult.riskCategory}"
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0288D1),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Submit",
                                        style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium)
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    )
}



