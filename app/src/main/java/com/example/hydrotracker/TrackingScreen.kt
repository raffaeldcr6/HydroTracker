package com.example.hydrotracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hydrotracker.data.model.WaterIntake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LogMinum(val waktu: String, val judul: String, val jumlah: Int)

@Composable
fun TrackingScreen(
    waters: List<WaterIntake>,
    totalIntake: Int,
    onIntakeChanged: (Int) -> Unit,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val logList = remember { mutableStateListOf<LogMinum>() }
    val targetIntake = 2000
    val progress = (totalIntake.toFloat() / targetIntake.toFloat()).coerceAtMost(1f)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Tambah Custom Intake") },
            text = {
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text("Jumlah (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customAmount.toIntOrNull() ?: 0
                        if (amount > 0) {
                            scope.launch {
                                isLoading = true
                                delay(1000)
                                onIntakeChanged(totalIntake + amount)
                                val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                logList.add(LogMinum(now, "Custom Intake", amount))
                                isLoading = false
                                showDialog = false
                                customAmount = ""
                                snackbarHostState.showSnackbar("Berhasil menambahkan $amount ml!")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) { Text("Tambah") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F9FF)),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF1976D2)).padding(20.dp)
                ) {
                    Column {
                        Text("Tracking Minum", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Catat asupan airmu hari ini", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalIntake ml", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("dari $targetIntake ml", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(10.dp),
                                    color = Color(0xFF80DEEA),
                                    trackColor = Color.White.copy(alpha = 0.3f),
                                    strokeCap = StrokeCap.Round
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${(progress * 100).toInt()}% dari target harian", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pilih Ukuran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        waters.take(3).forEach { water ->
                            if (water.amountMl > 0) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isLoading = true
                                            delay(800)
                                            onIntakeChanged(totalIntake + water.amountMl)
                                            val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                            logList.add(LogMinum(now, water.title, water.amountMl))
                                            isLoading = false
                                            snackbarHostState.showSnackbar("${water.title} (${water.amountMl} ml) ditambahkan!")
                                        }
                                    },
                                    enabled = !isLoading,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("${water.amountMl}ml", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (logList.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Riwayat Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0D47A1))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                items(logList.reversed()) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💧", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(log.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(log.waktu, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text("+${log.jumlah} ml", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                        }
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💧", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada catatan hari ini", color = Color.Gray, fontSize = 14.sp)
                            Text("Mulai catat minumanmu!", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = Color(0xFF1976D2)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp))
    }
}