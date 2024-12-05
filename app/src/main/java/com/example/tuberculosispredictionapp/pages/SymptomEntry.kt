package com.example.tuberculosispredictionapp.pages

data class SymptomEntry(
    val symptom: String,
    val timestamp: String,
    val confidence: Float = 0f,         // Prediction percentage
    val riskCategory: String = "Low"   // Risk level
)

