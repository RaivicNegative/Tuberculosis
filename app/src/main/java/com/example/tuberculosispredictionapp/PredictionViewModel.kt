package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tuberculosispredictionapp.pages.SymptomEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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




    fun getPredictionResult(selectedSymptoms: List<String>): PredictionResult {
        val confidence = (0..100).random().toFloat() / 100
        val disease = if (selectedSymptoms.contains("Cough(>3weeks)") || selectedSymptoms.contains("Blood In Sputum")) {
            "Tuberculosis"
        } else {
            "No Tuberculosis"
        }

        val riskCategory = when {
            confidence == 1.0f -> "Confirmed Diagnosis"
            confidence > 0.80 -> "Very High Risk"
            confidence > 0.50 -> "High Risk"
            confidence > 0.20 -> "Medium Risk"
            confidence > 0.01 -> "Low Risk"
            else -> "No Risk"
        }

        return PredictionResult(disease, confidence, riskCategory)
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
