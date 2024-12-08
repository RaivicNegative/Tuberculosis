package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authstate = MutableLiveData<Authstate>()
    val authstate: LiveData<Authstate> = _authstate

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authstate.value = Authstate.Unauthenticated
        } else {
            _authstate.value = Authstate.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authstate.value = Authstate.Error("Email or password can't be empty")
            return
        }

        _authstate.value = Authstate.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authstate.value = Authstate.Authenticated
                } else {
                    _authstate.value = Authstate.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun register(
        email: String,
        password: String,
        fullname: String,
        address: String,
        phonenumber: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || fullname.isEmpty() || address.isEmpty() || phonenumber.isEmpty()) {
            callback(false, "Please fill in all fields")
            return
        }

        _authstate.value = Authstate.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val profile = hashMapOf(
                            "fullname" to fullname,
                            "address" to address,
                            "phonenumber" to phonenumber,
                            "email" to email
                        )

                        val usersRef = FirebaseDatabase.getInstance().getReference("profile")
                        usersRef.child(userId).setValue(profile)
                            .addOnSuccessListener {
                                _authstate.value = Authstate.Authenticated
                                callback(true, "Registration Successful")
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthViewModel", "Error saving profile: ${e.message}")
                                callback(false, "Error saving profile: ${e.message}")
                            }
                    } else {
                        callback(false, "User authentication failed")
                    }
                } else {
                    callback(false, "Error: ${task.exception?.message}")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authstate.value = Authstate.Unauthenticated
    }
}

sealed class Authstate {
    object Authenticated : Authstate()
    object Unauthenticated : Authstate()
    object Loading : Authstate()
    data class Error(val message: String) : Authstate()
}
