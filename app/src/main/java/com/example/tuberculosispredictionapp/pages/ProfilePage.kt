package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuberculosispredictionapp.AuthViewModel
import com.example.tuberculosispredictionapp.ProfileViewModel
import com.example.tuberculosispredictionapp.R
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfilePage(modifier: Modifier, profileViewModel: ProfileViewModel, authViewModel: AuthViewModel) {
    var saveMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            profileViewModel.getUserProfile(userEmail)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDF7FB))
    ) {
        if (profileViewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        profileViewModel.updateUserProfile(
                            profileViewModel.email,
                            profileViewModel.phoneNumber,
                            profileViewModel.address,
                            profileViewModel.password
                        )
                        saveMessage = "Profile saved successfully!"
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0288D1),
                        contentColor = Color(0xFF1C1C1C)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.Black,
                        style = TextStyle(
                            fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                if (saveMessage.isNotEmpty()) {
                    Text(text = saveMessage, color = Color.Green)
                    LaunchedEffect(saveMessage) {

                        kotlinx.coroutines.delay(3000)
                        saveMessage = ""
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Profile",
                    fontSize = 28.sp,
                    style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.pp),
                    contentDescription = "Profile",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {

                        TextField(
                            value = profileViewModel.email,
                            onValueChange = { newText -> profileViewModel.email = newText },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    "Email",
                                    fontSize = 16.sp,
                                    style = TextStyle(
                                        fontFamily = customRobotoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = profileViewModel.phoneNumber,
                            onValueChange = { newText -> profileViewModel.phoneNumber = newText },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    "Phone Number",
                                    fontSize = 16.sp,
                                    style = TextStyle(
                                        fontFamily = customRobotoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = profileViewModel.address,
                            onValueChange = { newText -> profileViewModel.address = newText },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    "Address",
                                    fontSize = 16.sp,
                                    style = TextStyle(
                                        fontFamily = customRobotoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = profileViewModel.password,
                            onValueChange = { newText -> profileViewModel.password = newText },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    "Password",
                                    fontSize = 16.sp,
                                    style = TextStyle(
                                        fontFamily = customRobotoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon = if (passwordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                }
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = null)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                authViewModel.logout()
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0288D1),
                                contentColor = Color(0xFF1C1C1C)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Logout",
                                color = Color.Black,
                                style = TextStyle(
                                    fontFamily = customRobotoFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}