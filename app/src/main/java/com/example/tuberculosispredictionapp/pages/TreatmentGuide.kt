package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.tuberculosispredictionapp.pages.customRobotoFontFamily

@Composable
fun TreatmentGuide(navController: NavController) {

    var isContentVisible1 by remember { mutableStateOf(false) }
    var isContentVisible2 by remember { mutableStateOf(false) }
    var isContentVisible3 by remember { mutableStateOf(false) }
    var isContentVisible4 by remember { mutableStateOf(false) }
    var isContentVisible5 by remember { mutableStateOf(false) }

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

        Text(text = "Treatment Guide",
            fontSize = 30.sp,
            style = TextStyle(fontFamily = customRobotoFontFamily,
                fontWeight = FontWeight.Bold))

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Drug-Resistant Tuberculosis",
                        fontSize = 20.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-Medications: Second-line drugs (e.g., Fluoroquinolones, Injectable agents)" +
                                    "-Duration: 18-24 months or longer" +
                                    "-Monitoring: Needs a specialized treatment plan",
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium
                            ),
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pulmonary Tuberculosis",
                        fontSize = 20.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-Medications: Isoniazid, Rifampicin, Ethambutol, Pyrazinamide" +
                                    "-Duration: 6-9 months" +
                                    "-Monitoring: Regular check-ups and tests",
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium
                            ),
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Extrapulmonary Tuberculosis",
                        fontSize = 20.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-Medications: Same as pulmonary TB" +
                                    "-Duration: 6-12 months, depending on the affected area" +
                                    "-Considerations: May need specialist care",
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium
                            ),
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Latent Tuberculosis Infection",
                        fontSize = 20.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible4) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-Medications: Isoniazid for 6-9 months\", \"Rifampicin for 4 months\", \"Isoniazid and Rifapentine for 12 weeks" +
                                    "-Duration: Varies based on treatment regimen" +
                                    "-Goal: Prevent active TB",
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = { isContentVisible5 = !isContentVisible5 },
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Active Tuberculosis",
                        fontSize = 20.sp,
                        style = TextStyle(fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    if (isContentVisible5) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-Medications: Same as pulmonary TB" +
                                    "-Duration: 6-12 months" +
                                    "-Adherence: Directly Observed Therapy (DOT) is recommended",
                            style = TextStyle(
                                fontFamily = customRobotoFontFamily,
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
