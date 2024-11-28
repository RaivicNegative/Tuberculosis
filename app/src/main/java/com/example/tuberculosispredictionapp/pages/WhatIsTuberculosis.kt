package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tuberculosispredictionapp.R

@Composable
fun WhatIsTuberculosis(navController: NavController) {
    var isContentVisible1 by remember { mutableStateOf(false) }
    var isContentVisible2 by remember { mutableStateOf(false) }
    var isContentVisible3 by remember { mutableStateOf(false) }
    var isContentVisible4 by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDF7FB))
    ) {
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Image(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = { isContentVisible1 = !isContentVisible1 },
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81D4FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "What is Tuberculosis?",
                        fontSize = 22.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- Tuberculosis (TB) is an infectious disease that most often affects the lungs. " +
                                    "TB is caused by a type of bacteria. It spreads through the air when infected " +
                                    "people cough, sneeze, or spit.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = { isContentVisible2 = !isContentVisible2 },
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81D4FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Transmission",
                        fontSize = 22.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- TB is spread through the air from one person to another. TB germs are passed " +
                                    "through the air when someone who is sick with TB disease of the lungs or throat " +
                                    "coughs, speaks, laughs, sings, or sneezes.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = { isContentVisible3 = !isContentVisible3 },
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81D4FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Types of Tuberculosis",
                        fontSize = 22.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- Pulmonary Tuberculosis: This is the most common type, affecting the lungs. Symptoms include a persistent cough, chest pain, and coughing up blood.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "- Extrapulmonary Tuberculosis: This occurs outside the lungs and can affect various parts of the body.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "- Latent Tuberculosis Infection (LTBI): This means a person has TB bacteria in their body but doesn’t have symptoms and can’t spread it to others.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "- Active Tuberculosis: This is when the TB bacteria are active and causing symptoms.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "- Drug-Resistant Tuberculosis: This occurs when TB bacteria don't respond to standard medications.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = { isContentVisible4 = !isContentVisible4 },
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81D4FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Symptoms",
                        fontSize = 22.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible4) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- A prolonged cough, sometimes accompanied by blood, can be concerning, especially when paired with chest pain, weakness, and fatigue. These symptoms, along with noticeable weight loss, fever, and night sweats, may indicate a serious underlying condition that requires prompt medical attention.",
                            style = TextStyle(fontFamily = customRobotoFontFamily, fontWeight = FontWeight.Medium),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
