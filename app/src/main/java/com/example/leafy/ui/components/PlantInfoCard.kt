package com.example.leafy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.leafy.model.ScanResult

@Composable
fun PlantInfoCard(result: ScanResult) {
    Card(modifier = Modifier.padding(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = result.commonName ?: "Desconocido",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = result.scientificName ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Precisión: ${"%.1f".format((result.confidence ?: 0.0) * 100)}%",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = result.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}