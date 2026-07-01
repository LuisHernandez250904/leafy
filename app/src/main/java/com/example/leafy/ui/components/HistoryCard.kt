package com.example.leafy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.leafy.model.PlantHistory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryCard(
    history: PlantHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🌿 Imagen de la planta
            AsyncImage(
                model = history.imageUrl,
                contentDescription = history.plantName,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(Modifier.width(12.dp))

            // 📄 Información
            Column(modifier = Modifier.weight(1f)) {

                // Nombre común (o científico si no hay)
                Text(
                    text = history.commonName.ifBlank { history.plantName },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0B4D1E)
                )

                // Nombre científico
                if (history.plantName.isNotBlank()) {
                    Text(
                        text = history.plantName,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                // Familia (si existe)
                if (history.familyName.isNotBlank()) {
                    Text(
                        text = "Family: ${history.familyName}",
                        fontSize = 12.sp,
                        color = Color(0xFF6A6A6A)
                    )
                }

                // Confianza
                if (history.confidence > 0.0) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Confidence: ${(history.confidence * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = Color(0xFF00B45F),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Fecha del análisis
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Date: ${formatDate(history.scanDate)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// 🕒 Función para convertir timestamp a fecha legible
fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}