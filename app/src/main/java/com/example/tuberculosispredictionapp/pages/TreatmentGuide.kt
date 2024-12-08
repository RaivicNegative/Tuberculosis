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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentGuide(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Treatment Guide",
                        fontSize = 30.sp,
                        style = TextStyle(
                            fontFamily = customRobotoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            colorFilter = ColorFilter.tint(Color.Black)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    ) {
                        val treatmentData = listOf(
                            "Drug-Resistant Tuberculosis" to "- Medications: Second-line drugs (e.g., Fluoroquinolones, Injectable agents)\n" +
                                    "- Duration: 18-24 months or longer\n" +
                                    "- Monitoring: Needs a specialized treatment plan",
                            "Pulmonary Tuberculosis" to "- Medications: Isoniazid, Rifampicin, Ethambutol, Pyrazinamide\n" +
                                    "- Duration: 6-9 months\n" +
                                    "- Monitoring: Regular check-ups and tests",
                            "Extrapulmonary Tuberculosis" to "- Medications: Same as pulmonary TB\n" +
                                    "- Duration: 6-12 months, depending on the affected area\n" +
                                    "- Considerations: May need specialist care",
                            "Latent Tuberculosis Infection" to "- Medications: Isoniazid for 6-9 months, Rifampicin for 4 months, or Isoniazid and Rifapentine for 12 weeks\n" +
                                    "- Duration: Varies based on treatment regimen\n" +
                                    "- Goal: Prevent active TB",
                            "Active Tuberculosis" to "- Medications: Same as pulmonary TB\n" +
                                    "- Duration: 6-12 months\n" +
                                    "- Adherence: Directly Observed Therapy (DOT) is recommended"
                        )

                        treatmentData.forEach { (title, content) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                elevation = CardDefaults.elevatedCardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = title,
                                        fontSize = 20.sp,
                                        style = TextStyle(
                                            fontFamily = customRobotoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.elevatedCardElevation(2.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(
                                                0xFFE8F5E9
                                            )
                                        )
                                    ) {
                                        Text(
                                            text = content,
                                            style = TextStyle(
                                                fontFamily = customRobotoFontFamily,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(16.dp),
                                            color = Color.Black
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


