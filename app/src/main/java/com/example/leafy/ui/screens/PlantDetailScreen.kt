package com.example.leafy.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.leafy.firebase.FirestoreRepository
import com.example.leafy.model.PlantHistory
import com.example.leafy.ui.components.TopBar
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun PlantDetailScreen(
    historyId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { FirestoreRepository() }

    var item by remember { mutableStateOf<PlantHistory?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(historyId) {
        try {
            isLoading = true
            error = null
            item = repo.getHistoryItemById(historyId)
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar detalle"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Detalle de planta",
                onBack = onBack
            )
        },
        containerColor = Color(0xFFF5F6FA)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = error ?: "Ocurrió un error",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                item == null -> {
                    Text(
                        text = "No se encontró el registro.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    val history = item!!
                    val formattedDate = if (history.scanDate > 0L) {
                        SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(history.scanDate))
                    } else {
                        "Fecha desconocida"
                    }
                    val confidencePercent = (history.confidence * 100).toInt()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // ---------- HERO CARD CON IMAGEN ----------
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column {
                                Image(
                                    painter = rememberAsyncImagePainter(history.imageUrl),
                                    contentDescription = "Imagen de la planta",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 28.dp,
                                                topEnd = 28.dp
                                            )
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF00C26F).copy(alpha = 0.06f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                        .padding(18.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = history.commonName.ifBlank { "Sin nombre común" },
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = history.plantName.ifBlank { "Nombre científico desconocido" },
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color(0xFF4A4A4A),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // ---------- CHIPS DE INFO RÁPIDA ----------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            InfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Verified,
                                label = "Coincidencia",
                                value = "$confidencePercent%"
                            )
                            InfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.FamilyRestroom,
                                label = "Familia",
                                value = history.familyName.ifBlank { "Sin familia" }
                            )
                            InfoChip(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.CalendarToday,
                                label = "Fecha",
                                value = formattedDate
                            )
                        }

                        // ---------- DESCRIPCIÓN / DETALLE ----------
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Información de la especie",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (history.description.isNotBlank()) {
                                    Text(
                                        text = history.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF4A4A4A)
                                    )
                                } else {
                                    Text(
                                        text = "No se ha agregado una descripción para esta planta.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // ---------- BOTÓN COMPARTIR ----------
                        Button(
                            onClick = {
                                val shareText = """
                                    Mira esta planta que identifiqué con Leafy 🌿

                                    Nombre común: ${history.commonName.ifBlank { "Sin nombre común" }}
                                    Nombre científico: ${history.plantName.ifBlank { "Desconocido" }}
                                    Familia: ${history.familyName.ifBlank { "Sin familia" }}
                                    Coincidencia: $confidencePercent%

                                    Fecha: $formattedDate
                                """.trimIndent()

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Compartir planta")
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00C26F)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Compartir",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ---------- COMPONENTE AUXILIAR PARA CHIPS ----------

@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00C26F),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}