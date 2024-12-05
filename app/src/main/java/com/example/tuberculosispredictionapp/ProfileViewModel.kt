package com.example.tuberculosispredictionapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

data class UserProfile(
    val fullname: String = "",
    val email: String = "",
    val phonenumber: String = "",
    val address: String = "",
    val password: String = ""
)

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("profile")

    init {
        fetchProfileData()
    }

    fun fetchProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated."
            Log.e("ProfileViewModel", "User not authenticated.")
            return
        }

        _isLoading.value = true

        val userRef = usersRef.child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false
                if (snapshot.exists()) {
                    val fullname = snapshot.child("fullname").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val phonenumber = snapshot.child("phonenumber").getValue(String::class.java) ?: ""
                    val address = snapshot.child("address").getValue(String::class.java) ?: ""
                    val password = snapshot.child("password").getValue(String::class.java) ?: ""

                    _userProfile.value = UserProfile(fullname, address, phonenumber, email, password)
                } else {
                    _errorMessage.value = "User profile not found."
                    Log.e("ProfileViewModel", "User profile not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = "Error fetching user profile: ${error.message}"
                Log.e("ProfileViewModel", "Error fetching user profile: ${error.message}")
            }
        })
    }

    fun updateProfileData(fullname: String, address: String, phonenumber: String, email: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated."
            Log.e("ProfileViewModel", "User not authenticated.")
            return
        }

        _isLoading.value = true

        val userRef = usersRef.child(userId)

        val updatedProfile = mapOf(
            "fullname" to fullname,
            "email" to email,
            "phonenumber" to phonenumber,
            "address" to address

        )

        userRef.updateChildren(updatedProfile)
            .addOnSuccessListener {
                _isLoading.value = false
                Log.d("ProfileViewModel", "Profile updated successfully.")
                fetchProfileData()
            }
            .addOnFailureListener { error ->
                _isLoading.value = false
                _errorMessage.value = "Error updating profile: ${error.message}"
                Log.e("ProfileViewModel", "Error updating profile: ${error.message}")
            }
    }

    fun clearProfileData(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated."
            Log.e("ProfileViewModel", "User not authenticated.")
            onFailure(Exception("User not authenticated"))
            return
        }

        _isLoading.value = true

        val userRef = usersRef.child(userId)
        userRef.removeValue()
            .addOnSuccessListener {
                _isLoading.value = false
                Log.d("ProfileViewModel", "Profile cleared successfully.")
                _userProfile.value = UserProfile()
                onSuccess()
            }
            .addOnFailureListener { error ->
                _isLoading.value = false
                _errorMessage.value = "Error clearing profile: ${error.message}"
                Log.e("ProfileViewModel", "Error clearing profile: ${error.message}")
                onFailure(error)
            }
    }

    fun refreshProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated."
            Log.e("ProfileViewModel", "User not authenticated.")
            return
        }

        _isLoading.value = true

        val userRef = usersRef.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false
                if (snapshot.exists()) {
                    val fullname = snapshot.child("fullname").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val phonenumber = snapshot.child("phonenumber").getValue(String::class.java) ?: ""
                    val address = snapshot.child("address").getValue(String::class.java) ?: ""
                    val password = snapshot.child("password").getValue(String::class.java) ?: ""

                    _userProfile.value = UserProfile(fullname, email, phonenumber, address, password)
                    Log.d("ProfileViewModel", "Profile refreshed successfully.")
                } else {
                    _errorMessage.value = "User profile not found."
                    Log.e("ProfileViewModel", "User profile not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = "Error refreshing profile: ${error.message}"
                Log.e("ProfileViewModel", "Error refreshing profile: ${error.message}")
            }
        })
    }

}
