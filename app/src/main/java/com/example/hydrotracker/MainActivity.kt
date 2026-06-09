package com.example.hydrotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.hydrotracker.data.model.WaterIntake
import com.example.hydrotracker.ui.screen.*
import com.example.hydrotracker.ui.theme.HydroTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HydroTrackerTheme {
                val navController = rememberNavController()
                var totalIntake by remember { mutableIntStateOf(0) }
                var waters by remember { mutableStateOf<List<WaterIntake>>(emptyList()) }

                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { WelcomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("main/{nama}") { backStackEntry ->
                        val nama = backStackEntry.arguments?.getString("nama") ?: "User"
                        MainScreen(
                            nama = nama,
                            totalIntake = totalIntake,
                            waters = waters,
                            onReset = { totalIntake = 0 },
                            onIntakeChanged = { totalIntake = it },
                            onWatersLoaded = { waters = it },
                            onLogout = {
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

object UserSession {
    private val users = mutableMapOf<String, Pair<String, String>>()
    var namaPengguna = ""
    var emailPengguna = ""

    fun register(nama: String, email: String, password: String) {
        users[email] = Pair(nama, password)
        namaPengguna = nama
        emailPengguna = email
    }

    fun login(email: String, password: String): Boolean {
        val user = users[email] ?: return false
        return if (user.second == password) {
            namaPengguna = user.first
            emailPengguna = email
            true
        } else false
    }
}

@Composable
fun MainScreen(
    nama: String,
    totalIntake: Int,
    waters: List<WaterIntake>,
    onReset: () -> Unit,
    onIntakeChanged: (Int) -> Unit,
    onWatersLoaded: (List<WaterIntake>) -> Unit,
    onLogout: () -> Unit
) {
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = innerNavController.currentBackStackEntryAsState().value?.destination?.route
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = { innerNavController.navigate("dashboard") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = currentRoute == "info",
                    onClick = { innerNavController.navigate("info") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Info") }
                )
                NavigationBarItem(
                    selected = currentRoute == "tracking",
                    onClick = { innerNavController.navigate("tracking") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Tracking") }
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { innerNavController.navigate("profile") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profil") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    navController = innerNavController,
                    nama = nama,
                    totalIntake = totalIntake,
                    onReset = onReset,
                    onWatersLoaded = onWatersLoaded
                )
            }
            composable("info") { InfoKesehatanScreen() }
            composable("tracking") {
                TrackingScreen(
                    waters = waters,
                    totalIntake = totalIntake,
                    onIntakeChanged = onIntakeChanged,
                    navController = innerNavController
                )
            }
            composable("detail/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title")
                val water = waters.find { it.title == title }
                if (water != null) {
                    DetailScreen(
                        food = water,
                        navController = innerNavController,
                        isFullScreen = true,
                        onConfirm = { amount -> onIntakeChanged(totalIntake + amount) }
                    )
                }
            }
            composable("profile") {
                ProfileScreen(
                    nama = nama,
                    navController = innerNavController,
                    onLogout = onLogout
                )
            }
        }
    }
}