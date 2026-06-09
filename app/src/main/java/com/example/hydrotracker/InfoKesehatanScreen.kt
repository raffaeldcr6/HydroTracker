package com.example.hydrotracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InfoKesehatan(
    val emoji: String,
    val judul: String,
    val isi: String,
    val warna: Color,
    val warnaText: Color = Color(0xFF1565C0)
)

@Composable
fun InfoKesehatanScreen() {
    val infoList = listOf(
        InfoKesehatan("💧", "Minum 8 Gelas Sehari", "Kebutuhan air harian rata-rata orang dewasa adalah 8 gelas atau sekitar 2 liter. Kebutuhan ini bisa berbeda tergantung aktivitas dan kondisi tubuh.", Color(0xFFE3F2FD)),
        InfoKesehatan("🧠", "Hidrasi & Konsentrasi", "Dehidrasi ringan sebesar 1-2% dari berat badan dapat menurunkan konsentrasi, mempengaruhi suasana hati, dan menyebabkan sakit kepala.", Color(0xFFE8F5E9), Color(0xFF1B5E20)),
        InfoKesehatan("💪", "Air & Metabolisme", "Minum air putih sebelum makan dapat membantu mengontrol nafsu makan dan meningkatkan metabolisme hingga 30% selama 1-1.5 jam.", Color(0xFFFFF3E0), Color(0xFFE65100)),
        InfoKesehatan("🌡️", "Tanda Dehidrasi", "Urin berwarna gelap, pusing, mulut kering, dan kelelahan adalah tanda-tanda dehidrasi. Segera minum air putih jika mengalaminya.", Color(0xFFFCE4EC), Color(0xFFC62828)),
        InfoKesehatan("⏰", "Waktu Terbaik Minum Air", "Minum segelas air saat bangun tidur, 30 menit sebelum makan, setelah olahraga, dan sebelum tidur sangat dianjurkan untuk kesehatan optimal.", Color(0xFFF3E5F5), Color(0xFF4A148C)),
        InfoKesehatan("🏃", "Air & Olahraga", "Saat berolahraga, tubuh kehilangan cairan melalui keringat. Minum 500ml air 2 jam sebelum olahraga dan 150-250ml setiap 15-20 menit selama olahraga.", Color(0xFFE0F7FA), Color(0xFF006064)),
        InfoKesehatan("🌿", "Air vs Minuman Lain", "Air putih adalah pilihan terbaik untuk hidrasi. Minuman berkafein seperti kopi dan teh dapat menyebabkan dehidrasi jika dikonsumsi berlebihan.", Color(0xFFF9FBE7), Color(0xFF33691E))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F9FF)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1976D2)).padding(20.dp)
            ) {
                Column {
                    Text("Info Kesehatan", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Fakta penting tentang hidrasi tubuh", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(infoList) { info ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = info.warna),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(info.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(info.judul, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = info.warnaText)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(info.isi, fontSize = 14.sp, color = Color(0xFF424242), lineHeight = 22.sp)
                }
            }
        }
    }
}