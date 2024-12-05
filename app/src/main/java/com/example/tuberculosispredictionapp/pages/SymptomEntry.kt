package com.example.tuberculosispredictionapp.pages

data class SymptomEntry(
    val symptom: String,
    val timestamp: String,
    val confidence: Float = 0f,
    val riskCategory: String = "Low"
)

