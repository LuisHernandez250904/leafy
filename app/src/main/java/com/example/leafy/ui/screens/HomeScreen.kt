package com.example.leafy.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.leafy.model.ScanResult
import com.example.leafy.ui.components.PlantInfoCard
import com.example.leafy.viewmodel.PlantScanViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(
    recentResults: List<ScanResult> = emptyList(),
    onScanPressed: () -> Unit,      // ya no se usan, se dejan por compatibilidad
    onGalleryPressed: () -> Unit,
    onExplorePressed: () -> Unit,
    onHistoryPressed: () -> Unit,
    onProfilePressed: () -> Unit
) {
    val context = LocalContext.current
    val scanViewModel: PlantScanViewModel = viewModel()

    var capturedFile by remember { mutableStateOf<File?>(null) } // temp para PlantNet
    var capturedUri by remember { mutableStateOf<Uri?>(null) }   // URI (galería o temp)
    var isAnalyzing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launcher de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // 1) Intentamos guardar en GALERÍA (persistente)
            val galleryUri = saveBitmapToGallerySafe(context, bitmap)

            // 2) Creamos archivo temporal para PlantNet
            val tempFile = saveBitmapToTempFile(context, bitmap)

            capturedFile = tempFile

            // Si galería falló, usamos el Uri del archivo temporal
            capturedUri = galleryUri ?: tempFile.toUri()

            errorMessage = if (galleryUri == null) {
                "No se pudo guardar en la galería, pero la planta se puede analizar."
            } else {
                null
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                // INICIO
                NavigationBarItem(
                    selected = true,
                    onClick = { /* ya estás en Inicio */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") }
                )

                // MIS PLANTAS
                NavigationBarItem(
                    selected = false,
                    onClick = onHistoryPressed,
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Mis Plantas") }
                )

                // EXPLORAR
                NavigationBarItem(
                    selected = false,
                    onClick = onExplorePressed,
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Explorar") }
                )

                // PERFIL
                NavigationBarItem(
                    selected = false,
                    onClick = onProfilePressed,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                // -------- TÍTULO --------
                Text(
                    text = "LeafLens",
                    fontSize = 28.sp,
                    color = Color(0xFF0B4D1E)
                )
                Text(
                    text = "Descubre el mundo de las plantas",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                // -------- CARD VERDE --------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF00D37A),
                                        Color(0xFF00B766),
                                        Color(0xFF009D57)
                                    )
                                )
                            )
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Icono
                        Box(
                            modifier = Modifier
                                .size(85.dp)
                                .background(
                                    Color.White.copy(alpha = 0.15f),
                                    RoundedCornerShape(42.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        Text(
                            "Identifica cualquier planta",
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            "Toma una foto para descubrir la especie",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(Modifier.height(22.dp))

                        // Botón TOMAR FOTO
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color(0xFF1F6F43)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Tomar foto", color = Color(0xFF1F6F43))
                        }

                        Spacer(Modifier.height(12.dp))

                        // Botón ANALIZAR PLANTA
                        Button(
                            onClick = {
                                val file = capturedFile
                                val uri = capturedUri
                                if (file != null && uri != null) {
                                    analyzeImageInHome(
                                        viewModel = scanViewModel,
                                        file = file,
                                        displayUri = uri,
                                        onGoToHistory = onHistoryPressed,
                                        onStateChange = { loading, error ->
                                            isAnalyzing = loading
                                            errorMessage = error
                                        }
                                    )
                                } else {
                                    errorMessage = "Primero toma una foto de la planta."
                                }
                            },
                            enabled = capturedFile != null && !isAnalyzing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Analizar planta", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // -------- PREVIEW DE LA FOTO TOMADA --------
                capturedUri?.let { uri ->
                    Text(
                        text = "Vista previa de la planta:",
                        fontSize = 14.sp,
                        color = Color(0xFF0B4D1E)
                    )
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto de la planta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Spacer(Modifier.height(20.dp))
                }

                // -------- PRO TIP --------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF5D7)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFFE6B000)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Consejo útil", color = Color(0xFFE6B000))
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Para mejores resultados, toma fotos claras con buena iluminación y enfoca hojas o flores.",
                            fontSize = 13.sp,
                            color = Color(0xFF6A6A6A)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // -------- LISTA --------
                Text(
                    "Resultados recientes",
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))

                if (recentResults.isEmpty()) {
                    Text("No hay escaneos recientes.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentResults) { result ->
                            PlantInfoCard(result)
                        }
                    }
                }

                // Mensaje de error simple
                errorMessage?.let { msg ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Overlay de carga
            if (isAnalyzing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Analizando la planta...",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ------------------ LÓGICA DE ANÁLISIS DESDE HOME ------------------

private fun analyzeImageInHome(
    viewModel: PlantScanViewModel,
    file: File,
    displayUri: Uri,
    onGoToHistory: () -> Unit,
    onStateChange: (loading: Boolean, error: String?) -> Unit
) {
    onStateChange(true, null)
    viewModel.analyzeAndSave(
        imageFile = file,
        displayUri = displayUri
    ) { history ->
        if (history != null) {
            onStateChange(false, null)
            onGoToHistory()
        } else {
            onStateChange(false, "No se pudo analizar la planta. Intenta de nuevo.")
        }
    }
}

// --------------- HELPERS PARA GUARDAR LA IMAGEN -------------------

/**
 * Intenta guardar el bitmap en la GALERÍA (Pictures/LeafLens).
 * Si falla, devuelve null y NO lanza excepción.
 */
private fun saveBitmapToGallerySafe(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "leaflens_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/LeafLens"
            )
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (uri == null) {
            Log.e("HomeScreen", "No se pudo insertar en MediaStore (uri=null)")
            null
        } else {
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                out.flush()
            }
            uri
        }
    } catch (e: Exception) {
        Log.e("HomeScreen", "Error guardando en galería: ${e.message}", e)
        null
    }
}

/**
 * Guarda un bitmap como archivo TEMPORAL en cacheDir para enviarlo a PlantNet.
 */
private fun saveBitmapToTempFile(context: Context, bitmap: Bitmap): File {
    val dir = File(context.cacheDir, "plant_temp")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "plant_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        out.flush()
    }
    return file
}
