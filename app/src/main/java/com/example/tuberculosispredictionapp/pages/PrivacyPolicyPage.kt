package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyPage(modifier: Modifier) {

    val visibilityStates = remember { mutableStateOf(mapOf(
        1 to false, 2 to false, 3 to false, 4 to false, 5 to false, 6 to false, 7 to false
    )) }

    fun toggleVisibility(section: Int) {
        visibilityStates.value = visibilityStates.value.toMutableMap().apply {
            this[section] = !(this[section] ?: false)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy Policy", fontSize = 36.sp,
                    style = TextStyle(fontFamily = customRobotoFontFamily,
                        fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF81C784)),
            )
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6F2F1))
                    .padding(paddingValues)
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC3EFC5)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                            .padding(bottom = 80.dp)
                    ) {
                        Text(
                            text = "Privacy Policy for Tuberculosis Prediction Application",
                            fontSize = 22.sp,
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Effective Date: October 22, 2024",
                            fontSize = 18.sp,
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1976D2)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Hey there! Welcome to Tuberculosis Prediction Application. We’re dedicated to keeping your personal information safe and secure. This Privacy Policy explains how we collect, use, and protect your data when you use our app designed to help predict the likelihood of tuberculosis.",
                            fontSize = 18.sp,
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFF424242)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        for (i in 1..7) {
                            TextButton(onClick = { toggleVisibility(i) }) {
                                Text(
                                    text = getSectionTitle(i),
                                    color = Color(0xFF1976D2),
                                    fontSize = 16.sp,
                                    style = TextStyle(
                                        fontFamily = customRobotoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            if (visibilityStates.value[i] == true) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .background(Color(0xFFF1F8E9))
                                        .clip(RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = getSectionContent(i),
                                        style = TextStyle(fontFamily = customRobotoFontFamily),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    )
}

fun getSectionTitle(section: Int): String {
    return when (section) {
        1 -> "What Information We Collect?"
        2 -> "How We Use Your Information?"
        3 -> "Sharing Your Information"
        4 -> "Keeping Your Data Safe"
        5 -> "Your Rights"
        6 -> "Changes to This Policy"
        7 -> "Get in Touch"
        else -> ""
    }
}

fun getSectionContent(section: Int): String {
    return when (section) {
        1 -> "-Your Personal Info: When you use our app, you might share some personal details like your name, email, and any other information you choose to provide.\n" +
                "-Health Details: To give you the best predictions, we may ask for health-related information, such as your symptoms and medical history.\n" +
                "-How You Use the App: We also collect data on how you interact with the app like your device type and how often you use it. This helps us improve your experience!"
        2 -> "-To Provide Our Services: We want to offer you accurate predictions based on the information you share.\n" +
                "-To Improve Our App: We’re always looking to make things better and add new features.\n" +
                "-To Communicate with You: We might reach out if you have questions or need support.\n" +
                "-To Stay Compliant: We follow the law and protect your rights."
        3 -> "-With Service Providers: Sometimes we work with trusted third-party services to help us run the app smoothly. They only get the information they need to do their job.\n" +
                "-For Legal Reasons: If required by law, we might share your information to comply with regulations or respond to authorities."
        4 -> "-We take security seriously and use various measures to protect your information. However, no online service is completely secure, and while we strive to protect your data, we can’t guarantee absolute security."
        5 -> "-ACCESS: You can request to see what information we have about you.\n" +
                "-CORRECTION: If something is wrong, you can ask us to fix it.\n" +
                "-DELETION: You can request to have your information deleted.\n" +
                "-CONTROL: You can object to how we process your data."
        6 -> "-We may update this Privacy Policy from time to time to keep it fresh and relevant. When we do, we’ll let you know by updating the effective date at the top."
        7 -> "If you have any questions, concerns, or just want to chat about our privacy practices, feel free to contact us:\n" +
                "-Email: tbpredictapp@gmail.com\n" +
                "-Address: Inabanga, Bohol, Philippines"
        else -> ""
    }
}
