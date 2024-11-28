package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var isLoading by mutableStateOf(false)

    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var address by mutableStateOf("")
    var password by mutableStateOf("")

    fun getUserProfile(userEmail: String) {
        isLoading = true

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.e("ProfileViewModel", "No user found")
                } else {
                    val user = result.documents[0]
                    email = user.getString("email") ?: ""
                    phoneNumber = user.getString("phoneNumber") ?: ""
                    address = user.getString("address") ?: ""
                    password = user.getString("password") ?: ""
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Error getting user profile", exception)
                isLoading = false
            }
    }

    fun updateUserProfile(newEmail: String, newPhoneNumber: String, newAddress: String, newPassword: String) {
        val user = hashMapOf(
            "email" to newEmail,
            "phoneNumber" to newPhoneNumber,
            "address" to newAddress,
            "password" to newPassword
        )

        db.collection("users")
            .document(newEmail)
            .set(user)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "User profile updated")
            }
            .addOnFailureListener { e ->
                Log.w("ProfileViewModel", "Error updating document", e)
            }
    }
}