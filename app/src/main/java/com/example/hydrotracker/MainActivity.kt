package com.example.hydrotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.hydrotracker.data.model.WaterIntake
import com.example.hydrotracker.data.api.RetrofitClient
import com.example.hydrotracker.ui.theme.HydroTrackerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.hydrotracker.data.repository.WaterRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HydroTrackerTheme {
                val navController = rememberNavController()
                var totalIntake by remember { mutableIntStateOf(0) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        navController = navController,
                        totalIntake = totalIntake,
                        onIntakeChanged = { totalIntake = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    totalIntake: Int,
    onIntakeChanged: (Int) -> Unit
) {
    var waters by remember { mutableStateOf<List<WaterIntake>>(emptyList()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            WaterTrackerApp(
                navController = navController,
                totalIntake = totalIntake,
                onReset = { onIntakeChanged(0) },
                onWatersLoaded = { waters = it }
            )
        }
        composable("detail/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            val water = waters.find { it.title == title }
            if (water != null) {
                DetailScreen(
                    food = water,
                    navController = navController,
                    isFullScreen = true,
                    onConfirm = { amount -> onIntakeChanged(totalIntake + amount) }
                )
            }
        }
    }
}

@Composable
fun WaterTrackerApp(
    navController: NavHostController,
    totalIntake: Int,
    onReset: () -> Unit,
    onWatersLoaded: (List<WaterIntake>) -> Unit
) {
    val repository = remember { WaterRepository() }
    var waters by remember { mutableStateOf<List<WaterIntake>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val targetIntake = 2000

    LaunchedEffect(Unit) {
        try {
            waters = repository.getWaterIntakes()
            onWatersLoaded(waters)
            isError = waters.isEmpty()
        } catch (e: Exception) {
            isError = true
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (isError || waters.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Gagal Memuat Data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pastikan koneksi internet Anda menyala",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "HydroTracker",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Target Harian: $targetIntake ml",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    text = "Rekomendasi Pilihan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(waters) { water ->
                        RecommendedWaterItem(food = water, navController = navController)
                    }
                }
            }
        }

        item {
            Text(text = "Daftar Menu Air", style = MaterialTheme.typography.titleLarge)
        }

        items(waters) { water ->
            WaterCard(water = water, navController = navController)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progress Harian",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val progress = (totalIntake.toFloat() / targetIntake.toFloat()).coerceAtMost(1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.secondary,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$totalIntake / $targetIntake ml",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = onReset,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    food: WaterIntake,
    navController: NavHostController,
    isFullScreen: Boolean = false,
    onConfirm: (Int) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreen) PaddingValues(0.dp) else PaddingValues(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.title,
                    placeholder = painterResource(id = R.drawable.air_0),
                    error = painterResource(id = R.drawable.air_0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = food.title, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Kapasitas: ${food.amountMl} ml", fontSize = 18.sp)
            Text(text = food.description, color = Color.Gray)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        delay(2000)
                        onConfirm(food.amountMl)
                        isLoading = false
                        snackbarHostState.showSnackbar("Berhasil menambahkan ${food.title}!")
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Memproses...")
                } else {
                    Text("Konfirmasi Minum")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isFullScreen) navController.popBackStack()
                    else navController.navigate("detail/${food.title}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isFullScreen) "Kembali" else "Pesan")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun RecommendedWaterItem(food: WaterIntake, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { navController.navigate("detail/${food.title}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.title,
                placeholder = painterResource(id = R.drawable.air_0),
                error = painterResource(id = R.drawable.air_0),
                modifier = Modifier.fillMaxWidth().height(90.dp).padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = food.title,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WaterCard(water: WaterIntake, navController: NavHostController) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            Column {
                AsyncImage(
                    model = water.imageUrl,
                    contentDescription = water.title,
                    placeholder = painterResource(id = R.drawable.air_0),
                    error = painterResource(id = R.drawable.air_0),
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = water.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = water.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Button(
                        onClick = { navController.navigate("detail/${water.title}") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Detail")
                    }
                }
            }
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}