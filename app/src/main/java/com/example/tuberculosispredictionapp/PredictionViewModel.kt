package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tuberculosispredictionapp.pages.SymptomEntry
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

    // In your ViewModel
    private val _symptomsHistory = MutableStateFlow<List<SymptomEntry>>(emptyList())
    val symptomsHistory: StateFlow<List<SymptomEntry>> = _symptomsHistory


    init {
        fetchPredictions()
    }

    private fun fetchPredictions() {
        predictionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PredictionViewModel", "Symptoms data: ${snapshot.child("symptoms").value}")

                val fetchedPredictions = mutableListOf<Prediction>()
                for (child in snapshot.children) {
                    val id = child.key.orEmpty()

                    val symptomsSnapshot = child.child("symptoms")
                    val symptoms = mutableListOf<Int>()
                    for (symptom in symptomsSnapshot.children) {
                        val symptomValue = symptom.getValue(Long::class.java) ?: 0L
                        symptoms.add(symptomValue.toInt())
                    }

                    val prediction: String = child.child("prediction").let {
                        when (val value = it.getValue()) {
                            is String -> value
                            is Long -> value.toString()
                            else -> "Unknown"
                        }
                    }

                    fetchedPredictions.add(
                        Prediction(
                            id = id,
                            symptoms = symptoms,
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

    fun fetchSymptomsFromDatabase(userId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users/$userId/symptoms")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val symptomsList = mutableListOf<SymptomEntry>()

                for (childSnapshot in snapshot.children) {
                    val symptom = childSnapshot.child("symptom").getValue(String::class.java)
                    val timestamp = childSnapshot.child("timestamp").getValue(String::class.java)

                    // Only add to list if both symptom and timestamp exist
                    if (symptom != null && timestamp != null) {
                        val symptomEntry = SymptomEntry(symptom, timestamp)
                        symptomsList.add(symptomEntry)
                    }
                }

                // Update the symptoms history StateFlow
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

    fun savePrediction(userId: String, symptoms: List<Int>, predictionResult: PredictionResult) {
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
                timestamp = dateFormat.format(currentTimestamp)  // Format the timestamp
            )

            // Saving to Firebase
            val newSymptomRef = symptomsRef.push()  // Generates a unique ID for each symptom
            newSymptomRef.setValue(entry).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Symptom saved: $entry")
                } else {
                    Log.e("Firebase", "Error saving symptom", task.exception)
                }
            }
        }
    }

    fun listenForPredictionUpdates(userId: String) {
        val predictionRef = predictionsRef.child(userId)

        predictionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PredictionViewModel", "Symptoms data: ${snapshot.child("symptoms").value}")

                val symptomsSnapshot = snapshot.child("symptoms")
                val symptoms = mutableListOf<Int>()
                for (symptom in symptomsSnapshot.children) {
                    val symptomValue = symptom.getValue(Long::class.java) ?: 0L
                    symptoms.add(symptomValue.toInt())
                }

                val prediction: String = snapshot.child("prediction").let {
                    when (val value = it.getValue()) {
                        is String -> value
                        is Long -> value.toString()
                        else -> "Unknown"
                    }
                }

                val confidence = snapshot.child("confidence").getValue(Float::class.java) ?: 0f
                val riskCategory = snapshot.child("riskCategory").getValue(String::class.java).orEmpty()

                val result = PredictionResult(prediction, confidence, riskCategory)
                _predictionResult.value = result
                Log.d("PredictionViewModel", "Updated Prediction for $userId: $result")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PredictionViewModel", "Error listening to prediction updates for $userId: ${error.message}")
            }
        })
    }
}
