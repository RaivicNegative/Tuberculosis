package com.example.tuberculosispredictionapp.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.tuberculosispredictionapp.PredictionViewModel
import com.example.tuberculosispredictionapp.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.roundToInt

data class Disease(val name: String, val symptoms: Map<String, Int>)
data class PredictionResult(val disease: String, val confidence: Float, val riskCategory: String)

fun loadModelFile(context: Context, modelFileName: String): Interpreter {
    val assetFileDescriptor = context.assets.openFd(modelFileName)
    val fileInputStream = assetFileDescriptor.createInputStream()
    val fileChannel = fileInputStream.channel
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength

    val modelByteBuffer = fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    return Interpreter(modelByteBuffer)
}

fun getPredictedDisease(symptoms: List<String>, context: Context): PredictionResult {
    val totalFeatures = 354
    val symptomIndices = mapOf(
        "Cough(>3weeks)" to 0,
        "Bloodinsputum" to 1,
        "Chestpain" to 2,
        "Nightsweats" to 3,
        "Weightloss" to 4,
        "Fever" to 5,
        "Fatigue" to 6,
        "Lossofappetite" to 7,
        "Others" to 8
    )

    if (symptoms.isEmpty()) {
        Log.d("Prediction", "No symptoms selected.")
        return PredictionResult("No Symptoms", 0f, "No Risk")
    }

    val inputData = FloatArray(totalFeatures) { index ->
        val symptom = symptomIndices.entries.find { it.value == index }?.key
        if (symptom != null && symptoms.contains(symptom)) 1f else 0f
    }

    Log.d("Prediction", "Input Data: ${inputData.joinToString()}")

    val model = loadModelFile(context, "tuberculosis_model.tflite")
    val inputTensor = ByteBuffer.allocateDirect(4 * inputData.size).apply {
        order(ByteOrder.nativeOrder())
        inputData.forEach { putFloat(it) }
    }
    val outputBuffer = ByteBuffer.allocateDirect(4).apply { order(ByteOrder.nativeOrder()) }

    return try {
        model.run(inputTensor, outputBuffer)
        outputBuffer.rewind()
        val confidence = outputBuffer.float

        val riskCategory = when {
            confidence >= 0.84 -> "Extremely High Risk"
            confidence >= 0.40 -> "Very High Risk"
            confidence >= 0.21 -> "Moderate Risk"
            confidence > 0.01 -> "Low Risk"
            else -> "No Risk"
        }

        PredictionResult("Tuberculosis", confidence, riskCategory)
    } catch (e: Exception) {
        Log.e("Prediction Error", "Error during prediction: ${e.message}")
        PredictionResult("Error", 0f, "Unknown")
    } finally {
        model.close()
    }
}

@Composable
fun PredictTuberculosis(navController: NavController, viewModel: PredictionViewModel = viewModel()) {
    var selectedSymptoms by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1F5FE))
    ) {
        Column(
            modifier = Modifier.padding(2.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
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
                            .fillMaxWidth()
                            .padding(2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Predict Tuberculosis",
                                fontSize = 24.sp,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Image(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = "Predict Image",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "*Please click the box if you experience these symptoms below:",
                        fontSize = 16.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        val symptoms = listOf(
                            "Cough(>3weeks)",
                            "Blood In Sputum",
                            "Chest Pain",
                            "Night Sweats",
                            "Weight Loss",
                            "Fever",
                            "Fatigue",
                            "Loss of Appetite",
                            "Others"
                        )

                        symptoms.forEach { symptom ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                    text = symptom, fontSize = 16.sp,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {

                                    Log.d("Prediction", "User Input: $selectedSymptoms")
                                    val predictionResult = getPredictedDisease(selectedSymptoms, context)

                                    viewModel.setPredictionResult(predictionResult)
                                    viewModel.setHasPredicted(true)

                                    val roundedConfidence = (predictionResult.confidence * 100).roundToInt()
                                    Log.d("Prediction", "Confidence: ${predictionResult.confidence}, Rounded: $roundedConfidence")

                                    Log.d("Prediction", "Result: ${predictionResult.disease}, Confidence: ${predictionResult.confidence}, Risk: ${predictionResult.riskCategory}")

                                    navController.navigate("result/${predictionResult.disease}/$roundedConfidence/${predictionResult.riskCategory}")


                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0288D1),
                                    contentColor = Color(0xFF1C1C1C)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Submit",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}