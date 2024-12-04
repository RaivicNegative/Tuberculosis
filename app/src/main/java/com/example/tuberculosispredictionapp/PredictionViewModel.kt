package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tuberculosispredictionapp.pages.SymptomEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class Prediction(
    val id: String = "",
    val symptoms: List<Int> = emptyList(),
    val prediction: String = ""
)

data class PredictionResult(
    val disease: String = "Tuberculosis",
    val confidence: Float = 0f,
    val riskCategory: String = "Low"
)

class PredictionViewModel : ViewModel() {

    private val _predictions = mutableStateOf<List<Prediction>>(emptyList())
    val predictions: State<List<Prediction>> = _predictions

    private val _predictionResult = MutableLiveData<PredictionResult>()
    val predictionResult: LiveData<PredictionResult> = _predictionResult

    private val _hasPredicted = MutableLiveData<Boolean>()
    val hasPredicted: LiveData<Boolean> = _hasPredicted

    private val database = FirebaseDatabase.getInstance()
    private val predictionsRef = database.getReference("predictions")

    private val _symptomsHistory = MutableStateFlow<List<SymptomEntry>>(emptyList())
    val symptomsHistory: StateFlow<List<SymptomEntry>> = _symptomsHistory

    init {
        fetchPredictions()
    }

    private fun fetchPredictions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            return
        }

        predictionsRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PredictionViewModel", "Fetched Snapshot: ${snapshot.value}")

                val fetchedPredictions = mutableListOf<Prediction>()

                if (snapshot.exists()) {

                    val prediction = snapshot.child("prediction").getValue(String::class.java).orEmpty()
                    val confidence = snapshot.child("confidence").getValue(Double::class.java) ?: 0.0
                    val riskCategory = snapshot.child("riskCategory").getValue(String::class.java).orEmpty()

                    fetchedPredictions.add(
                        Prediction(
                            id = userId,
                            prediction = prediction
                        )
                    )
                }

                _predictions.value = fetchedPredictions
                Log.d("PredictionViewModel", "Fetched Predictions: $fetchedPredictions")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PredictionViewModel", "Error fetching predictions: ${error.message}")
            }
        })
    }


    fun fetchSymptomsFromDatabase(userId1: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("PredictionViewModel", "User not authenticated.")
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/symptoms")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val symptomsList = mutableListOf<SymptomEntry>()
                for (childSnapshot in snapshot.children) {
                    val symptom = childSnapshot.child("symptom").getValue(String::class.java)
                    val timestamp = childSnapshot.child("timestamp").getValue(String::class.java)
                    if (symptom != null && timestamp != null) {
                        symptomsList.add(SymptomEntry(symptom, timestamp))
                    }
                }
                _symptomsHistory.value = symptomsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching symptoms: ${error.message}")
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

        val predictionData = mapOf(
            "symptoms" to symptoms,
            "prediction" to predictionResult.disease,
            "confidence" to predictionResult.confidence,
            "riskCategory" to predictionResult.riskCategory
        )

        predictionsRef.child(userId).setValue(predictionData)
            .addOnSuccessListener {
                Log.d("PredictionViewModel", "Prediction saved successfully.")
            }
            .addOnFailureListener { error ->
                Log.e("PredictionViewModel", "Error saving prediction: ${error.message}")
            }
    }


    fun saveSymptomsToDatabase(userId: String, selectedSymptoms: List<String>) {
        val symptomsRef = FirebaseDatabase.getInstance().getReference("users/$userId/symptoms")

        val currentTimestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
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
}
