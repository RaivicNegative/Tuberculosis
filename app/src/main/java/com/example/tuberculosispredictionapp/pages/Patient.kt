package com.example.tuberculosispredictionapp.pages

data class Patient(
    val email: String = "",
    val password: String = "",
    val fullname: String = "",
    val phonenumber: Int,
    val address: String = ""
)
