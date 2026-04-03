package com.example.hydrotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hydrotracker.model.WaterDataSource
import com.example.hydrotracker.model.WaterIntake
import com.example.hydrotracker.ui.theme.HydroTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HydroTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterTrackerApp()
                }
            }
        }
    }
}

@Composable
fun WaterTrackerApp() {
    var totalIntake by remember { mutableIntStateOf(0) }
    val targetIntake = 2000

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
                    items(WaterDataSource.dummyWater) { water ->
                        RecommendedWaterItem(water = water)
                    }
                }
            }
        }

        item {
            Text(
                text = "Daftar Menu Air",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(WaterDataSource.dummyWater) { water ->
            WaterCard(water = water, onAdd = { amount ->
                totalIntake += amount
            })
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
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
                            onClick = { totalIntake = 0 },
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
fun RecommendedWaterItem(water: WaterIntake) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = water.imageRes),
                contentDescription = water.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = water.title,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WaterCard(water: WaterIntake, onAdd: (Int) -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            Column {
                Image(
                    painter = painterResource(id = water.imageRes),
                    contentDescription = water.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = water.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = water.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = { onAdd(water.amountMl) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Tambah", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}