package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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

@Composable
fun Result(
    navController: NavController,
    disease: String,
    confidence: Int,
    riskCategory: String,
    viewModel: PredictionViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDF7FB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }

            Text(
                text = "Result",
                fontSize = 32.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            Text(
                text = "The percentage you have a chance of $disease is:",
                fontSize = 20.sp,
                style = TextStyle(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$riskCategory of Tuberculosis",
                    fontSize = 22.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        contentColor = Color(0xFF1C1C1C)
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
}
