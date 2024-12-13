package com.example.tuberculosispredictionapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tuberculosispredictionapp.pages.SymptomEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class CombinedEntry(
    val timestamp: String,
    val symptoms: List<SymptomEntry>,
    val prediction: String,
    val confidence: Float,
    val riskCategory: String
)

data class PredictionResult(
    val disease: String = "Tuberculosis",
    val confidence: Float = 0f,
    val riskCategory: String = "Low"
)

class PredictionViewModel : ViewModel() {

    private val _combinedHistory = MutableStateFlow<List<CombinedEntry>>(emptyList())
    val combinedHistory: StateFlow<List<CombinedEntry>> = _combinedHistory

    private val _predictionResult = MutableLiveData<PredictionResult>()
    val predictionResult: LiveData<PredictionResult> = _predictionResult

    private val _hasPredicted = MutableLiveData<Boolean>()
    val hasPredicted: LiveData<Boolean> = _hasPredicted

    private val database = FirebaseDatabase.getInstance()
    private val predictionsRef = database.getReference("predictions")

    private val _symptomsHistory = MutableStateFlow<List<SymptomEntry>>(emptyList())
    val symptomsHistory: StateFlow<List<SymptomEntry>> = _symptomsHistory

    init {
        fetchCombinedData()
    }

    private fun loadModelFile(context: Context, modelFileName: String): Interpreter {
        val assetFileDescriptor = context.assets.openFd(modelFileName)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength

        val modelByteBuffer = fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(modelByteBuffer)
    }

    fun fetchCombinedData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            return
        }

        val symptomsRef = FirebaseDatabase.getInstance().getReference("users/$userId/symptoms")
        val predictionsRef = FirebaseDatabase.getInstance().getReference("predictions/$userId")

        val inputDateFormat = SimpleDateFormat("EEEE, MMM d, yyyy h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val outputDateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        symptomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(symptomsSnapshot: DataSnapshot) {
                val symptomsMap = mutableMapOf<String, MutableList<SymptomEntry>>()

                for (childSnapshot in symptomsSnapshot.children) {
                    val symptom = childSnapshot.child("symptom").getValue(String::class.java)
                    val rawTimestamp = childSnapshot.child("timestamp").getValue(String::class.java)
                    Log.d("TimestampDebug", "Raw Timestamp: $rawTimestamp")

                    try {
                        val parsedDate = rawTimestamp?.let { inputDateFormat.parse(it) }
                        Log.d("ParsedDateDebug", "Parsed Date: $parsedDate")
                        val readableTimestamp = parsedDate?.let { outputDateFormat.format(it) }

                        if (symptom != null && readableTimestamp != null) {
                            symptomsMap.getOrPut(readableTimestamp) { mutableListOf() }
                                .add(SymptomEntry(symptom, readableTimestamp))
                        }
                    } catch (e: ParseException) {
                        Log.e("PredictionViewModel", "Error parsing timestamp: $rawTimestamp", e)
                    }
                }

                predictionsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(predictionsSnapshot: DataSnapshot) {
                        val combinedList = mutableListOf<CombinedEntry>()

                        for (childSnapshot in predictionsSnapshot.children) {
                            val rawTimestamp = childSnapshot.child("timestamp").getValue(String::class.java)
                            val prediction = childSnapshot.child("prediction").getValue(String::class.java).orEmpty()
                            val confidence = childSnapshot.child("confidence").getValue(Double::class.java)?.toFloat() ?: 0f
                            val riskCategory = childSnapshot.child("riskCategory").getValue(String::class.java).orEmpty()

                            try {
                                val parsedDate = rawTimestamp?.let { inputDateFormat.parse(it) }
                                Log.d("ParsedDateDebug", "Parsed Date: $parsedDate")

                                val readableTimestamp = parsedDate?.let { outputDateFormat.format(it) }

                                if (readableTimestamp != null) {
                                    val symptoms = symptomsMap[readableTimestamp].orEmpty()
                                    combinedList.add(
                                        CombinedEntry(
                                            timestamp = readableTimestamp,
                                            symptoms = symptoms,
                                            prediction = prediction,
                                            confidence = confidence,
                                            riskCategory = riskCategory
                                        )
                                    )
                                }
                            } catch (e: ParseException) {
                                Log.e("PredictionViewModel", "Error parsing timestamp: $rawTimestamp", e)
                            }
                        }

                        _combinedHistory.value = combinedList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("PredictionViewModel", "Error fetching predictions: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PredictionViewModel", "Error fetching symptoms: ${error.message}")
            }
        })
    }

    fun getPredictionResult(context: Context, selectedSymptoms: List<String>): PredictionResult {
        val totalFeatures = 44
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

        if (selectedSymptoms.isEmpty()) {
            Log.d("Prediction", "No symptoms selected.")
            return PredictionResult("No Symptoms", 0f, "No Risk")
        }

        val inputData = FloatArray(totalFeatures) { index ->
            val symptom = symptomIndices.entries.find { it.value == index }?.key
            if (symptom != null && selectedSymptoms.contains(symptom)) 1f else 0f
        }

        Log.d("Prediction", "Input Data: ${inputData.joinToString()}")

        val model = loadModelFile(context, "tuberculosis_trained_model.tflite")
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
                confidence >= 0.84 -> "Very High Risk"
                confidence >= 0.40 -> "High Risk"
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

    fun setPredictionResult(result: PredictionResult) {
        _predictionResult.value = result
    }

    fun setHasPredicted(hasPredicted: Boolean) {
        _hasPredicted.value = hasPredicted
    }

    fun savePrediction(symptoms1: String, symptoms: List<Int>, predictionResult: PredictionResult) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            return
        }

        val currentTimestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }
        val formattedTimestamp = dateFormat.format(currentTimestamp)

        val predictionData = mapOf(
            "symptoms" to symptoms,
            "prediction" to predictionResult.disease,
            "confidence" to predictionResult.confidence,
            "riskCategory" to predictionResult.riskCategory,
            "timestamp" to formattedTimestamp
        )

        predictionsRef.child(userId).push().setValue(predictionData)
            .addOnSuccessListener {
                Log.d("PredictionViewModel", "Prediction saved successfully with timestamp: $formattedTimestamp")
            }
            .addOnFailureListener { error ->
                Log.e("PredictionViewModel", "Error saving prediction: ${error.message}")
            }
    }

    fun saveSymptomsToDatabase(userId: String, selectedSymptoms: List<String>) {
        val symptomsRef = FirebaseDatabase.getInstance().getReference("users/$userId/symptoms")

        val currentTimestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Manila")
        }

        selectedSymptoms.forEach { symptom ->
            val entry = SymptomEntry(
                symptom = symptom,
                timestamp = dateFormat.format(currentTimestamp)
            )

            val newSymptomRef = symptomsRef.push()
            newSymptomRef.setValue(entry).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Symptom saved: $entry")
                } else {
                    Log.e("Firebase", "Error saving symptom", task.exception)
                }
            }
        }
    }

    fun listenForPredictionUpdates(toString: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            return
        }

        val predictionRef = predictionsRef.child(userId)
        predictionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val symptoms = snapshot.child("symptoms").children.mapNotNull {
                    it.getValue(Long::class.java)?.toInt()
                }
                val prediction: String = snapshot.child("prediction").getValue(String::class.java) ?: "Unknown"
                val confidence = snapshot.child("confidence").getValue(Float::class.java) ?: 0f
                val riskCategory = snapshot.child("riskCategory").getValue(String::class.java).orEmpty()

                _predictionResult.value = PredictionResult(prediction, confidence, riskCategory)
                Log.d("PredictionViewModel", "Updated Prediction for $userId: $_predictionResult")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PredictionViewModel", "Error listening to prediction updates for $userId: ${error.message}")
            }
        })
    }

    fun clearHistory(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            onFailure(Exception("User not authenticated"))
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")

        userRef.child("symptoms").removeValue()
            .addOnSuccessListener {
                Log.d("PredictionViewModel", "Symptoms history cleared successfully.")

                predictionsRef.child(userId).removeValue()
                    .addOnSuccessListener {
                        Log.d("PredictionViewModel", "Predictions history cleared successfully.")

                        _symptomsHistory.value = emptyList()
                        _combinedHistory.value = emptyList()
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        Log.e("PredictionViewModel", "Failed to clear predictions: ${error.message}")
                        onFailure(error)
                    }
            }
            .addOnFailureListener { error ->
                Log.e("PredictionViewModel", "Failed to clear symptoms: ${error.message}")
                onFailure(error)
            }
    }
}
