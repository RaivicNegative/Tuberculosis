package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.tuberculosispredictionapp.pages.PredictionResult

class PredictionViewModel : ViewModel() {

    private val _hasPredicted = mutableStateOf(false)
    val hasPredicted: State<Boolean> = _hasPredicted

    private val _predictionResult = mutableStateOf<PredictionResult?>(null)
    val predictionResult: State<PredictionResult?> = _predictionResult

    fun setHasPredicted(predicted: Boolean) {
        _hasPredicted.value = predicted
    }

    fun setPredictionResult(result: PredictionResult?) {
        _predictionResult.value = result
        Log.d("PredictionViewModel", "New Prediction Result: $result")
    }

    fun resetPrediction() {
        _hasPredicted.value = false
        _predictionResult.value = null
    }
}
