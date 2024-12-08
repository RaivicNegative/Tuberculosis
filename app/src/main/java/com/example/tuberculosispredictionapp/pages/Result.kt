package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tuberculosispredictionapp.PredictionViewModel
import com.example.tuberculosispredictionapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Result(
    navController: NavController,
    disease: String,
    confidence: Int,
    riskCategory: String,
    viewModel: PredictionViewModel = viewModel()
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Result",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF81C784)
                ),
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
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .height(70.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFB6DFF1)),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "The percentage you have a chance of Tuberculosis is:",
                                fontSize = 20.sp,
                                style = TextStyle(fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .height(450.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFB6DFF1)),
                            elevation = CardDefaults.cardElevation(6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularPercentageBar(
                                percentage = confidence / 100f,
                                color = Color(0xfff6444a),
                                size = 200.dp,
                                strokeWidth = 12.dp,
                                text = "$confidence%",
                                textSize = 60
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "$riskCategory of Tuberculosis",
                                fontSize = 22.sp,
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setHasPredicted(true)
                                    navController.navigate("recommendation/true")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0288D1),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Recommendation",
                                    style = TextStyle(fontWeight = FontWeight.Medium)
                                )

                    }
                }
            }
        }
    )
}
