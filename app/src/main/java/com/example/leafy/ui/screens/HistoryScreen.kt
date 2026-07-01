package com.example.leafy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leafy.model.PlantHistory
import com.example.leafy.ui.components.HistoryCard
// Si MainBottomBar está en ui.components, descomenta esta línea:
// import com.example.leafy.ui.components.MainBottomBar
import com.example.leafy.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onItemClicked: (PlantHistory) -> Unit,
    onHomePressed: () -> Unit,
    onMyPlantsPressed: () -> Unit,
    onExplorePressed: () -> Unit,
    onProfilePressed: () -> Unit
) {
    val viewModel: HistoryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            MainBottomBar(
                current = BottomDestination.MY_PLANTS,
                onHomePressed = onHomePressed,
                onMyPlantsPressed = onMyPlantsPressed,
                onExplorePressed = onExplorePressed,
                onProfilePressed = onProfilePressed
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ================= HEADER =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF00C26F),
                                Color(0xFF00B45F)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Back + título
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "My Plants",
                                fontSize = 26.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.histories.size} species collected",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // BUSCADOR
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        placeholder = { Text("Search your plants...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            errorContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(18.dp))

                    // Botones GRID / LIST (visual)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.GridView,
                                contentDescription = "Grid view",
                                tint = Color(0xFF00C26F)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x33006F47)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "List view",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // ================= CONTENIDO =================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "Ocurrió un error",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.histories.isEmpty() -> {
                        EmptyMyPlantsUI()
                    }

                    else -> {
                        // 🔍 FILTRO: buscamos por nombre común, científico y familia
                        val query = searchQuery.trim()
                        val filtered = uiState.histories.filter { item ->
                            if (query.isBlank()) {
                                true
                            } else {
                                item.plantName.contains(query, ignoreCase = true) ||
                                        item.commonName.contains(query, ignoreCase = true) ||
                                        item.familyName.contains(query, ignoreCase = true)
                            }
                        }

                        if (filtered.isEmpty()) {
                            Text(
                                text = "No results for \"$searchQuery\"",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filtered) { item ->
                                    HistoryCard(
                                        history = item,
                                        onClick = { onItemClicked(item) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= EMPTY STATE =================

@Composable
fun EmptyMyPlantsUI() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6F7EF)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD4F3E3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color(0xFF74C89B),
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "No Plants Yet",
            fontSize = 18.sp,
            color = Color(0xFF2F3A3C),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Start identifying plants to build your collection",
            fontSize = 13.sp,
            color = Color(0xFF6E7C80)
        )
    }
}