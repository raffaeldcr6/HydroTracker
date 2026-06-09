package com.example.hydrotracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.hydrotracker.R
import com.example.hydrotracker.data.model.WaterIntake
import com.example.hydrotracker.data.repository.WaterRepository

@Composable
fun DashboardScreen(
    navController: NavHostController,
    nama: String,
    totalIntake: Int,
    onReset: () -> Unit,
    onWatersLoaded: (List<WaterIntake>) -> Unit
) {
    val repository = remember { WaterRepository() }
    var waters by remember { mutableStateOf<List<WaterIntake>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val targetIntake = 2000
    val progress = (totalIntake.toFloat() / targetIntake.toFloat()).coerceAtMost(1f)

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
            CircularProgressIndicator(color = Color(0xFF1976D2))
        }
        return
    }

    if (isError) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("😔", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Gagal Memuat Data", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pastikan koneksi internet Anda menyala", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F9FF)),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1976D2))
                    .padding(24.dp)
            ) {
                Column {
                    Text("Selamat Datang, $nama", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("HydroTracker", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Progress Harian", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("$totalIntake ml", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("dari $targetIntake ml target", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)),
                                color = Color(0xFF80DEEA),
                                trackColor = Color.White.copy(alpha = 0.3f),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${(progress * 100).toInt()}% tercapai", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                                TextButton(
                                    onClick = onReset,
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFFCDD2))
                                ) {
                                    Text("Reset", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tips Hari Ini 💡", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Text(
                        text = "💧 Minum segelas air putih setiap 2 jam sekali untuk menjaga tubuh tetap terhidrasi sepanjang hari!",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF1565C0),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Pilihan Cepat", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(waters) { water ->
                        Card(
                            modifier = Modifier.width(130.dp).clickable { navController.navigate("detail/${water.title}") },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = water.imageUrl,
                                    contentDescription = water.title,
                                    placeholder = painterResource(id = R.drawable.air_0),
                                    error = painterResource(id = R.drawable.air_0),
                                    modifier = Modifier.fillMaxWidth().height(80.dp).padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Text(water.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp), maxLines = 1)
                                Text("${water.amountMl} ml", fontSize = 11.sp, color = Color(0xFF1976D2), modifier = Modifier.padding(bottom = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Daftar Pilihan Air", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(waters) { water ->
            WaterCardItem(water = water, navController = navController)
        }
    }
}

@Composable
fun WaterCardItem(water: WaterIntake, navController: NavHostController) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(water.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(water.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${water.amountMl} ml", fontSize = 13.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { navController.navigate("detail/${water.title}") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Detail")
                    }
                }
            }
            IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}