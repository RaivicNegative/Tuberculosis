package com.example.tuberculosispredictionapp.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuberculosispredictionapp.AuthViewModel
import com.example.tuberculosispredictionapp.ProfileViewModel
import com.example.tuberculosispredictionapp.R
import com.example.tuberculosispredictionapp.UserProfile
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, profileViewModel: ProfileViewModel, authViewModel: AuthViewModel) {

    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.email ?: "") }
    var phonenumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val isLoading by profileViewModel.isLoading.observeAsState(false)
    val userProfile by profileViewModel.userProfile.observeAsState(UserProfile())
    val context = LocalContext.current

    LaunchedEffect(userProfile) {
        if (userProfile.email.isNotEmpty()) {
            fullname = userProfile.fullname
            email = userProfile.email
            phonenumber = userProfile.phonenumber
            address = userProfile.address
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontSize = 30.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { profileViewModel.refreshProfile() }) {
                        Icon(painter = painterResource(id = R.drawable.refresh), contentDescription = "Refresh",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF81C784)),
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEDF7FB))
                    .padding(paddingValues)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF95BCD5)
                    )
                } else {

                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(550.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFB6DFF1)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                TextButton(
                                    onClick = {
                                        profileViewModel.updateProfileData(
                                            fullname,
                                            email,
                                            phonenumber,
                                            address,
                                        )
                                        Toast.makeText(
                                            context,
                                            "Profile Updated",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "Save",
                                        style = TextStyle(
                                            color = Color(0xFF0288D1),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Spacer(modifier = Modifier.height(32.dp))

                                    OutlinedTextField(
                                        value = fullname,
                                        onValueChange = { fullname = it },
                                        enabled = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Fullname", fontSize = 16.sp,
                                            style = TextStyle(
                                                fontFamily = customRobotoFontFamily,
                                                fontWeight = FontWeight.Bold)) }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Email", fontSize = 16.sp,
                                            style = TextStyle(
                                                fontFamily = customRobotoFontFamily,
                                                fontWeight = FontWeight.Bold)) }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = phonenumber,
                                        onValueChange = { phonenumber = it },
                                        enabled = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Phone Number", fontSize = 16.sp,
                                            style = TextStyle(
                                                fontFamily = customRobotoFontFamily,
                                                fontWeight = FontWeight.Bold)) }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = address,
                                        onValueChange = { address = it },
                                        enabled = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Address", fontSize = 16.sp,
                                            style = TextStyle(
                                                fontFamily = customRobotoFontFamily,
                                                fontWeight = FontWeight.Bold)) }
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Button(
                                        onClick = { authViewModel.logout() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0288D1),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Logout",
                                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


