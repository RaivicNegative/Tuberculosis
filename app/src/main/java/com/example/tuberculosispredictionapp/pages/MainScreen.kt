package com.example.tuberculosispredictionapp.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tuberculosispredictionapp.AuthViewModel
import com.example.tuberculosispredictionapp.Authstate
import com.example.tuberculosispredictionapp.NavItem
import com.example.tuberculosispredictionapp.ProfileViewModel
import com.example.tuberculosispredictionapp.R
import com.google.api.ResourceDescriptor.History
import java.nio.file.WatchEvent

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, profileViewModel: ProfileViewModel) {

    val authstate = authViewModel.authstate.observeAsState()

    LaunchedEffect(authstate.value) {
        when (authstate.value) {
            is Authstate.Unauthenticated -> navController.navigate("Login")
            else -> Unit
        }
    }

    val navItemList = listOf(
        NavItem("Home", R.drawable.home),
        NavItem("Profile", R.drawable.person),
        NavItem("History", R.drawable.history),
        NavItem("PrivacyPolicy", R.drawable.policy)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFEDF7FB)
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = navItem.icon),
                                contentDescription = null)
                               },
                        label = {
                            Text(text = navItem.label, fontSize = 11.sp, style = TextStyle(fontFamily = customRobotoFontFamily))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF81C784),
                            unselectedIconColor = Color(0xFF52A6CE),
                            selectedTextColor = Color(0xFF81C784),
                            unselectedTextColor = Color(0xFF52A6CE)
                        )
                    )
                }
            }
        }

    ) { innerPadding ->
        when (selectedIndex) {
            0 -> HomePage(modifier = Modifier.padding(innerPadding), navController, authViewModel)
            1 -> ProfilePage(modifier = Modifier.padding(innerPadding), profileViewModel, authViewModel)
            2 -> HistoryScreen(modifier = Modifier.padding(innerPadding))
            3 -> PrivacyPolicyPage(modifier = Modifier.padding(innerPadding))
        }
    }
}